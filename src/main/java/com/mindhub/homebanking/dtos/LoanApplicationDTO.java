package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.models.Loan;

import java.util.List;

public class LoanApplicationDTO {


    private long loanId;
    private double amount;
    private Integer payments;
    private String toAccountNumber;


    //default constructor
    public LoanApplicationDTO() {
    }
    public LoanApplicationDTO(Long loanId, Double amount, Integer payments, String toAccountNumber) {
        this.loanId = loanId;
        this.amount = amount;
        this.payments = payments;
        this.toAccountNumber = toAccountNumber;
    }
    public long getLoanId() {
        return loanId;
    }

    public double getAmount() {
        return amount;
    }

    public Integer getPayments() {
        return payments;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    @Override
    public String toString() {
        return "LoanApplicationDTO{" +
                "loanId=" + loanId +
                ", maxAmount=" + amount +
                ", payments=" + payments +
                ", toAccountNumber='" + toAccountNumber + '\'' +
                '}';
    }
}
