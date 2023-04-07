package com.db.payamigo.services;

import com.db.payamigo.entity.Currency;
import com.db.payamigo.entity.Transaction;
import com.db.payamigo.entity.User;
import com.db.payamigo.entity.Wallet;
import com.db.payamigo.repository.TransactionRepository;
import com.db.payamigo.service.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class TransactionsServiceTest {
    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    private User payingUser, receivingUser;
    private Wallet sourceWallet, destinationWallet;
    private Transaction transaction1, transaction2;

    @Before
    public void setUp() {
        payingUser = new User(1, "Daniel", "daniel@gmail.com", "1234");
        receivingUser = new User(2, "Mihai", "mihai@gmail.com", "5678");

        sourceWallet = new Wallet(1, "Daniel's Wallet", 1000.0f, Currency.USD, payingUser);
        destinationWallet = new Wallet(2, "Mihai's Wallet", 2000.0f, Currency.EUR, receivingUser);

        transaction1 = new Transaction(1, 50.0f, 10.0f, 5.0f, Currency.EUR, new Date(), sourceWallet, destinationWallet);
        transaction2 = new Transaction(2, 20.0f, 5.0f, 1.0f, Currency.EUR, new Date(), sourceWallet, destinationWallet);
    }

    @Test
    public void testCreateTransaction() {
        when(transactionRepository.save(transaction1)).thenReturn(transaction1);

        Transaction createdTransaction = transactionService.createTransaction(transaction1);
        assertEquals(transaction1, createdTransaction);
    }

    @Test
    public void testGetAllTransactions() {
        when(transactionRepository.findAll()).thenReturn(Arrays.asList(transaction1, transaction2));

        List<Transaction> transactions = transactionService.getAllTransactions();
        assertEquals(2, transactions.size());
    }

    @Test
    public void testGetTransactionById() {
        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction1));

        Optional<Transaction> foundTransaction = transactionService.getTransactionById(1);
        assertEquals(transaction1, foundTransaction.orElse(null));
    }

    @Test
    public void testUpdateTransaction() {
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setAmount(40.0f);
        updatedTransaction.setCurrency(Currency.CNY);
        updatedTransaction.setCreatedAt(new Date());
        updatedTransaction.setSourceWallet(sourceWallet);
        updatedTransaction.setDestinationWallet(destinationWallet);

        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction1));
        when(transactionRepository.save(transaction1)).thenReturn(updatedTransaction);

        Transaction result = transactionService.updateTransaction(1, updatedTransaction);
        assertEquals(updatedTransaction, result);
    }

    @Test
    public void testDeleteTransaction() {
        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction1));
        transactionService.deleteTransaction(1);
        verify(transactionRepository, times(1)).deleteById(1);

        when(transactionRepository.findById(1)).thenReturn(Optional.empty());
        Optional<Transaction> deleteTransaction = transactionService.getTransactionById(1);
        assertEquals(Optional.empty(), deleteTransaction);
    }

    @Test
    public void testTransactionAmountHigherThanSourceWalletBalance() {
        transaction1.setAmount(4000.0f);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(transaction1);
        });

        assertEquals("Transaction amount higher than source wallet balance", exception.getMessage());
    }

    @Test
    public void testTransactionWithMissingDestinationWallet() {
        transaction1.setDestinationWallet(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(transaction1);
        });

        assertEquals("Destination wallet is required", exception.getMessage());
    }

    @Test
    public void testTransactionWithMissingSourceWallet() {
        transaction1.setSourceWallet(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(transaction1);
        });

        assertEquals("Source wallet is required", exception.getMessage());
    }

    @Test
    public void testUserPayingItself() {
        transaction1.setDestinationWallet(sourceWallet);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(transaction1);
        });

        assertEquals("User cannot pay itself", exception.getMessage());
    }

    @Test
    public void testTransactionWithNegativeAmount() {
        transaction1.setAmount(-100.0f);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(transaction1);
        });

        assertEquals("Transaction amount cannot be negative or null", exception.getMessage());
    }

    @Test
    public void testTransactionWithPastDate() {
        Date pastDate = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
        transaction1.setCreatedAt(pastDate);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(transaction1);
        });

        assertEquals("Transaction date cannot be in the past", exception.getMessage());
    }
}
