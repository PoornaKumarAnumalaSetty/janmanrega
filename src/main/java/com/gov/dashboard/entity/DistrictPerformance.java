package com.gov.dashboard.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "district_performance", indexes = {
    @Index(name = "idx_district_name", columnList = "district_name"),
    @Index(name = "idx_fin_year", columnList = "fin_year"),
    @Index(name = "idx_district_year", columnList = "district_name, fin_year")
})
public class DistrictPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fin_year")
    private String finYear;

    @Column(name = "month")
    private String month;

    @Column(name = "state_code")
    private String stateCode;

    @Column(name = "state_name")
    private String stateName;

    @Column(name = "district_code")
    private String districtCode;

    @Column(name = "district_name")
    private String districtName;

    @Column(name = "approved_labour_budget")
    private BigDecimal approvedLabourBudget;

    @Column(name = "average_wage_rate_per_day_per_person")
    private BigDecimal averageWageRatePerDayPerPerson;

    @Column(name = "average_days_of_employment_provided_per_household")
    private BigDecimal averageDaysOfEmploymentProvidedPerHousehold;

    @Column(name = "differently_abled_persons_worked")
    private BigDecimal differentlyAbledPersonsWorked;

    @Column(name = "material_and_skilled_wages")
    private BigDecimal materialAndSkilledWages;

    @Column(name = "number_of_completed_works")
    private BigDecimal numberOfCompletedWorks;

    @Column(name = "number_of_gps_with_nil_exp")
    private BigDecimal numberOfGpsWithNilExp;

    @Column(name = "number_of_ongoing_works")
    private BigDecimal numberOfOngoingWorks;

    @Column(name = "persondays_of_central_liability_so_far")
    private BigDecimal persondaysOfCentralLiabilitySoFar;

    @Column(name = "sc_persondays")
    private BigDecimal scPersondays;

    @Column(name = "sc_workers_against_active_workers")
    private BigDecimal scWorkersAgainstActiveWorkers;

    @Column(name = "st_persondays")
    private BigDecimal stPersondays;

    @Column(name = "st_workers_against_active_workers")
    private BigDecimal stWorkersAgainstActiveWorkers;

    @Column(name = "total_adm_expenditure")
    private BigDecimal totalAdmExpenditure;

    @Column(name = "total_exp")
    private BigDecimal totalExp;

    @Column(name = "total_households_worked")
    private BigDecimal totalHouseholdsWorked;

    @Column(name = "total_individuals_worked")
    private BigDecimal totalIndividualsWorked;

    @Column(name = "total_no_of_active_job_cards")
    private BigDecimal totalNoOfActiveJobCards;

    @Column(name = "total_no_of_active_workers")
    private BigDecimal totalNoOfActiveWorkers;

    @Column(name = "total_no_of_hhs_completed_100_days_of_wage_employment")
    private BigDecimal totalNoOfHhsCompleted100DaysOfWageEmployment;

    @Column(name = "total_no_of_jobcards_issued")
    private BigDecimal totalNoOfJobcardsIssued;

    @Column(name = "total_no_of_workers")
    private BigDecimal totalNoOfWorkers;

    @Column(name = "total_no_of_works_takenup")
    private BigDecimal totalNoOfWorksTakenup;

    @Column(name = "wages")
    private BigDecimal wages;

    @Column(name = "women_persondays")
    private BigDecimal womenPersondays;

    @Column(name = "percent_of_category_b_works")
    private BigDecimal percentOfCategoryBWorks;

    @Column(name = "percent_of_expenditure_on_agriculture_allied_works")
    private BigDecimal percentOfExpenditureOnAgricultureAlliedWorks;

    @Column(name = "percent_of_nrm_expenditure")
    private BigDecimal percentOfNrmExpenditure;

    @Column(name = "percentage_payments_generated_within_15_days")
    private BigDecimal percentagePaymentsGeneratedWithin15Days;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    // Constructors
    public DistrictPerformance() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFinYear() {
        return finYear;
    }

    public void setFinYear(String finYear) {
        this.finYear = finYear;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public BigDecimal getApprovedLabourBudget() {
        return approvedLabourBudget;
    }

    public void setApprovedLabourBudget(BigDecimal approvedLabourBudget) {
        this.approvedLabourBudget = approvedLabourBudget;
    }

    public BigDecimal getAverageWageRatePerDayPerPerson() {
        return averageWageRatePerDayPerPerson;
    }

    public void setAverageWageRatePerDayPerPerson(BigDecimal averageWageRatePerDayPerPerson) {
        this.averageWageRatePerDayPerPerson = averageWageRatePerDayPerPerson;
    }

    public BigDecimal getAverageDaysOfEmploymentProvidedPerHousehold() {
        return averageDaysOfEmploymentProvidedPerHousehold;
    }

    public void setAverageDaysOfEmploymentProvidedPerHousehold(BigDecimal averageDaysOfEmploymentProvidedPerHousehold) {
        this.averageDaysOfEmploymentProvidedPerHousehold = averageDaysOfEmploymentProvidedPerHousehold;
    }

    public BigDecimal getDifferentlyAbledPersonsWorked() {
        return differentlyAbledPersonsWorked;
    }

    public void setDifferentlyAbledPersonsWorked(BigDecimal differentlyAbledPersonsWorked) {
        this.differentlyAbledPersonsWorked = differentlyAbledPersonsWorked;
    }

    public BigDecimal getMaterialAndSkilledWages() {
        return materialAndSkilledWages;
    }

    public void setMaterialAndSkilledWages(BigDecimal materialAndSkilledWages) {
        this.materialAndSkilledWages = materialAndSkilledWages;
    }

    public BigDecimal getNumberOfCompletedWorks() {
        return numberOfCompletedWorks;
    }

    public void setNumberOfCompletedWorks(BigDecimal numberOfCompletedWorks) {
        this.numberOfCompletedWorks = numberOfCompletedWorks;
    }

    public BigDecimal getNumberOfGpsWithNilExp() {
        return numberOfGpsWithNilExp;
    }

    public void setNumberOfGpsWithNilExp(BigDecimal numberOfGpsWithNilExp) {
        this.numberOfGpsWithNilExp = numberOfGpsWithNilExp;
    }

    public BigDecimal getNumberOfOngoingWorks() {
        return numberOfOngoingWorks;
    }

    public void setNumberOfOngoingWorks(BigDecimal numberOfOngoingWorks) {
        this.numberOfOngoingWorks = numberOfOngoingWorks;
    }

    public BigDecimal getPersondaysOfCentralLiabilitySoFar() {
        return persondaysOfCentralLiabilitySoFar;
    }

    public void setPersondaysOfCentralLiabilitySoFar(BigDecimal persondaysOfCentralLiabilitySoFar) {
        this.persondaysOfCentralLiabilitySoFar = persondaysOfCentralLiabilitySoFar;
    }

    public BigDecimal getScPersondays() {
        return scPersondays;
    }

    public void setScPersondays(BigDecimal scPersondays) {
        this.scPersondays = scPersondays;
    }

    public BigDecimal getScWorkersAgainstActiveWorkers() {
        return scWorkersAgainstActiveWorkers;
    }

    public void setScWorkersAgainstActiveWorkers(BigDecimal scWorkersAgainstActiveWorkers) {
        this.scWorkersAgainstActiveWorkers = scWorkersAgainstActiveWorkers;
    }

    public BigDecimal getStPersondays() {
        return stPersondays;
    }

    public void setStPersondays(BigDecimal stPersondays) {
        this.stPersondays = stPersondays;
    }

    public BigDecimal getStWorkersAgainstActiveWorkers() {
        return stWorkersAgainstActiveWorkers;
    }

    public void setStWorkersAgainstActiveWorkers(BigDecimal stWorkersAgainstActiveWorkers) {
        this.stWorkersAgainstActiveWorkers = stWorkersAgainstActiveWorkers;
    }

    public BigDecimal getTotalAdmExpenditure() {
        return totalAdmExpenditure;
    }

    public void setTotalAdmExpenditure(BigDecimal totalAdmExpenditure) {
        this.totalAdmExpenditure = totalAdmExpenditure;
    }

    public BigDecimal getTotalExp() {
        return totalExp;
    }

    public void setTotalExp(BigDecimal totalExp) {
        this.totalExp = totalExp;
    }

    public BigDecimal getTotalHouseholdsWorked() {
        return totalHouseholdsWorked;
    }

    public void setTotalHouseholdsWorked(BigDecimal totalHouseholdsWorked) {
        this.totalHouseholdsWorked = totalHouseholdsWorked;
    }

    public BigDecimal getTotalIndividualsWorked() {
        return totalIndividualsWorked;
    }

    public void setTotalIndividualsWorked(BigDecimal totalIndividualsWorked) {
        this.totalIndividualsWorked = totalIndividualsWorked;
    }

    public BigDecimal getTotalNoOfActiveJobCards() {
        return totalNoOfActiveJobCards;
    }

    public void setTotalNoOfActiveJobCards(BigDecimal totalNoOfActiveJobCards) {
        this.totalNoOfActiveJobCards = totalNoOfActiveJobCards;
    }

    public BigDecimal getTotalNoOfActiveWorkers() {
        return totalNoOfActiveWorkers;
    }

    public void setTotalNoOfActiveWorkers(BigDecimal totalNoOfActiveWorkers) {
        this.totalNoOfActiveWorkers = totalNoOfActiveWorkers;
    }

    public BigDecimal getTotalNoOfHhsCompleted100DaysOfWageEmployment() {
        return totalNoOfHhsCompleted100DaysOfWageEmployment;
    }

    public void setTotalNoOfHhsCompleted100DaysOfWageEmployment(BigDecimal totalNoOfHhsCompleted100DaysOfWageEmployment) {
        this.totalNoOfHhsCompleted100DaysOfWageEmployment = totalNoOfHhsCompleted100DaysOfWageEmployment;
    }

    public BigDecimal getTotalNoOfJobcardsIssued() {
        return totalNoOfJobcardsIssued;
    }

    public void setTotalNoOfJobcardsIssued(BigDecimal totalNoOfJobcardsIssued) {
        this.totalNoOfJobcardsIssued = totalNoOfJobcardsIssued;
    }

    public BigDecimal getTotalNoOfWorkers() {
        return totalNoOfWorkers;
    }

    public void setTotalNoOfWorkers(BigDecimal totalNoOfWorkers) {
        this.totalNoOfWorkers = totalNoOfWorkers;
    }

    public BigDecimal getTotalNoOfWorksTakenup() {
        return totalNoOfWorksTakenup;
    }

    public void setTotalNoOfWorksTakenup(BigDecimal totalNoOfWorksTakenup) {
        this.totalNoOfWorksTakenup = totalNoOfWorksTakenup;
    }

    public BigDecimal getWages() {
        return wages;
    }

    public void setWages(BigDecimal wages) {
        this.wages = wages;
    }

    public BigDecimal getWomenPersondays() {
        return womenPersondays;
    }

    public void setWomenPersondays(BigDecimal womenPersondays) {
        this.womenPersondays = womenPersondays;
    }

    public BigDecimal getPercentOfCategoryBWorks() {
        return percentOfCategoryBWorks;
    }

    public void setPercentOfCategoryBWorks(BigDecimal percentOfCategoryBWorks) {
        this.percentOfCategoryBWorks = percentOfCategoryBWorks;
    }

    public BigDecimal getPercentOfExpenditureOnAgricultureAlliedWorks() {
        return percentOfExpenditureOnAgricultureAlliedWorks;
    }

    public void setPercentOfExpenditureOnAgricultureAlliedWorks(BigDecimal percentOfExpenditureOnAgricultureAlliedWorks) {
        this.percentOfExpenditureOnAgricultureAlliedWorks = percentOfExpenditureOnAgricultureAlliedWorks;
    }

    public BigDecimal getPercentOfNrmExpenditure() {
        return percentOfNrmExpenditure;
    }

    public void setPercentOfNrmExpenditure(BigDecimal percentOfNrmExpenditure) {
        this.percentOfNrmExpenditure = percentOfNrmExpenditure;
    }

    public BigDecimal getPercentagePaymentsGeneratedWithin15Days() {
        return percentagePaymentsGeneratedWithin15Days;
    }

    public void setPercentagePaymentsGeneratedWithin15Days(BigDecimal percentagePaymentsGeneratedWithin15Days) {
        this.percentagePaymentsGeneratedWithin15Days = percentagePaymentsGeneratedWithin15Days;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
