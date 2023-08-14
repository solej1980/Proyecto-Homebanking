package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class);
	}
	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository, ClientLoanRepository clientLoanRepository){
		return (args ->{
			Client client1 = new Client("Melba", "Morel", "melba@mindhub.com");
			clientRepository.save(client1);
			Account account1 = new Account("VIN001", LocalDate.now(), 5000 );
			Account account2 = new Account("VIN002", LocalDate.now().plusDays(1), 7500);
			client1.addAccount(account1);
			client1.addAccount(account2);
			accountRepository.save(account1);
			accountRepository.save(account2);
			clientRepository.save(client1);

			Client client2 = new Client("Maria", "Perez", "mperez@gmail.com");
			clientRepository.save(client2);
			Account account3 = new Account("VIN003", LocalDate.now().plusDays(1), 10000);
			client2.addAccount(account3);
			accountRepository.save(account3);
			clientRepository.save(client2);

			Transaction transaction1 = new Transaction(TransactionType.DEBIT, 547.90,"Google *youtube", LocalDateTime.now());
			Transaction transaction2 = new Transaction(TransactionType.CREDIT, 5000, "Transfer", LocalDateTime.now());
			Transaction transaction3 = new Transaction(TransactionType.CREDIT, 30000,"Deposit", LocalDateTime.now());
			Transaction transaction4 = new Transaction(TransactionType.DEBIT, 500, "ATM WDW ATM CASH", LocalDateTime.now());


			account1.addTransaction(transaction1);
			account2.addTransaction(transaction2);
			account3.addTransaction(transaction3);
			account3.addTransaction(transaction4);
			transactionRepository.save(transaction1);
			transactionRepository.save(transaction2);
			transactionRepository.save(transaction3);
			transactionRepository.save(transaction4);

			Loan loan1 = new Loan("Hipotecario", 500000, Arrays.asList(12, 24, 36, 48, 60));
			Loan loan2 = new Loan("Personal", 100000, Arrays.asList(6, 12, 24));
			Loan loan3 = new Loan("Automotriz", 300000, Arrays.asList(6, 12, 24, 36));
			loanRepository.save(loan1);
			loanRepository.save(loan2);
			loanRepository.save(loan3);

			ClientLoan clientLoan1 = new ClientLoan(400000, 60);
			ClientLoan clientLoan2 = new ClientLoan(50000, 12);
			ClientLoan clientLoan3 = new ClientLoan(100000, 24);
			ClientLoan clientLoan4 = new ClientLoan(200000, 36);
			client1.addClientLoan(clientLoan1);
			loan1.addClientLoan(clientLoan1);
			client1.addClientLoan(clientLoan2);
			loan2.addClientLoan(clientLoan2);
			client2.addClientLoan(clientLoan3);
			loan2.addClientLoan(clientLoan3);
			client2.addClientLoan(clientLoan4);
			loan3.addClientLoan(clientLoan4);
			loanRepository.save(loan1);
			loanRepository.save(loan2);
			loanRepository.save(loan3);

			clientLoanRepository.save(clientLoan1);
			clientLoanRepository.save(clientLoan2);
			clientLoanRepository.save(clientLoan3);
			clientLoanRepository.save(clientLoan4);


		});

	}

}
