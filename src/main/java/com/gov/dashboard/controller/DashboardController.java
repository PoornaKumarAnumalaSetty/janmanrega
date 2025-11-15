package com.gov.dashboard.controller;

import com.gov.dashboard.service.DashboardService;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private DashboardService dashboardService;
    
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/")
    public String index(Model model) {
        logger.info("Loading homepage");
        try {
            List<String> districts = dashboardService.findAllDistricts();
            model.addAttribute("districts", districts);
            model.addAttribute("totalDistricts", districts.size());
            model.addAttribute("currentYear", dashboardService.findLatestFinancialYear());
        } catch (Exception e) {
            logger.error("Error loading districts: {}", e.getMessage());
            model.addAttribute("districts", List.of());
            model.addAttribute("totalDistricts", 0);
            model.addAttribute("currentYear", "N/A");
        }
        return "index";
    }

    @GetMapping("/district/{districtName}")
    public String districtDashboard(@PathVariable String districtName, Model model) {
        logger.info("Loading dashboard for district: {}", districtName);
        try {
            Map<String, Object> summary = dashboardService.getDashboardSummary(districtName);
            model.addAttribute("summary", summary);
            model.addAttribute("districtName", districtName);
        } catch (Exception e) {
            logger.error("Error loading dashboard for district {}: {}", districtName, e.getMessage());
            model.addAttribute("error", "Unable to load dashboard data for " + districtName);
        }
        return "district";
    }

    @GetMapping("/chart/{districtName}")
    @ResponseBody
    public ResponseEntity<byte[]> getChartImage(@PathVariable String districtName) {
        logger.info("Generating chart for district: {}", districtName);
        try {
            byte[] chartImage = dashboardService.generateChartImage(districtName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(chartImage.length);

            return new ResponseEntity<>(chartImage, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error generating chart for district {}: {}", districtName, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/locate-district")
    @ResponseBody
    public ResponseEntity<Map<String, String>> locateDistrict(
            @RequestParam double lat,
            @RequestParam double lon) {
        logger.info("Attempting to locate district for coordinates: {}, {}", lat, lon);

        try {
            RestTemplate rt = restTemplate;
            String url = String.format("https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f&zoom=10&addressdetails=1", lat, lon);
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "janmanrega-dashboard/1.0 (contact: admin@example.com)");
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> resp = rt.exchange(url, HttpMethod.GET, entity, String.class);
            String body = resp.getBody();
            if (body == null) {
                return ResponseEntity.ok(Map.of("district", "", "matched", "false", "message", "no response"));
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(body);
            JsonNode address = root.path("address");
            String[] candidates = new String[] {
                    address.path("district").asText(null),
                    address.path("state_district").asText(null),
                    address.path("county").asText(null)
            };

            String detected = null;
            for (String c : candidates) {
                if (c != null && !c.isBlank()) { detected = c; break; }
            }

            if (detected == null) {
                return ResponseEntity.ok(Map.of("district", "", "matched", "false", "message", "not found"));
            }

            // Normalize and try to match with DB districts
            String norm = detected.toUpperCase().replace(" DISTRICT", "").replace(" ZILA", "").trim();
            List<String> all = dashboardService.findAllDistricts();
            String match = all.stream()
                    .filter(d -> d.equalsIgnoreCase(norm) || d.toUpperCase().contains(norm) || norm.contains(d.toUpperCase()))
                    .findFirst()
                    .orElse("");

            if (!match.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "district", match,
                        "matched", "true",
                        "message", "हम समझते हैं आप " + match + " में हैं"
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "district", detected,
                    "matched", "false",
                    "message", detected
            ));
        } catch (Exception ex) {
            logger.warn("Locate district failed: {}", ex.getMessage());
            return ResponseEntity.ok(Map.of("district", "", "matched", "false", "message", "error"));
        }
    }
}