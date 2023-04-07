package com.db.payamigo.services;

import com.db.payamigo.entity.Currency;
import com.db.payamigo.entity.User;
import com.db.payamigo.entity.Wallet;
import com.db.payamigo.repository.WalletRepository;
import com.db.payamigo.service.WalletService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class WalletsServiceTest {

    @InjectMocks
    private WalletService walletService;

    @Mock
    private WalletRepository walletRepository;

    private User user1, user2;
    private Wallet wallet1, wallet2;

    @Before
    public void setUp() {
        user1 = new User(1, "Daniel", "daniel@gmail.com", "1234");
        user2 = new User(2, "Mihai", "mihai@gmail.com", "5678");

        wallet1 = new Wallet(1, "Daniel's Wallet", 1000.0f, Currency.USD, user1);
        wallet2 = new Wallet(2, "Mihai's Wallet", 2000.0f, Currency.EUR, user2);
    }

    @Test
    public void testCreateWallet() {
        when(walletRepository.save(wallet1)).thenReturn(wallet1);

        Wallet createdWallet = walletService.createWallet(wallet1);
        assertEquals(wallet1, createdWallet);
    }

    @Test
    public void testGetAllWallets() {
        when(walletRepository.findAll()).thenReturn(Arrays.asList(wallet1, wallet2));

        List<Wallet> wallets = walletService.getAllWallets();
        assertEquals(2, wallets.size());
    }

    @Test
    public void testGetWalletById() {
        when(walletRepository.findById(1)).thenReturn(Optional.of(wallet1));

        Optional<Wallet> foundWallet = walletService.getWalletById(1);
        assertEquals(wallet1, foundWallet.orElse(null));
    }

    @Test
    public void testUpdateWallet() {
        Wallet updatedWallet = new Wallet();
        updatedWallet.setName("Daniel's Wallet");
        updatedWallet.setBalance(1500.0f);
        updatedWallet.setCurrency(Currency.CNY);
        updatedWallet.setUser(user1);

        when(walletRepository.findById(1)).thenReturn(Optional.of(wallet1));
        when(walletRepository.save(wallet1)).thenReturn(updatedWallet);

        Wallet result = walletService.updateWallet(1, updatedWallet);
        assertEquals(updatedWallet, result);
    }

    @Test
    public void testDeleteWallet() {
        when(walletRepository.findById(1)).thenReturn(Optional.of(wallet1));
        walletService.deleteWallet(1);
        verify(walletRepository, times(1)).deleteById(1);

        when(walletRepository.findById(1)).thenReturn(Optional.empty());
        Optional<Wallet> deletedWallet = walletService.getWalletById(1);
        assertEquals(Optional.empty(), deletedWallet);
    }

    @Test
    public void testWalletUnassociatedWithUser() {
        wallet1.setUser(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            walletService.createWallet(wallet1);
        });

        assertEquals("User is required", exception.getMessage());
    }

    @Test
    public void testNegativeBalance() {
        wallet1.setBalance(-100.0f);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            walletService.createWallet(wallet1);
        });

        assertEquals("Balance cannot be negative or null", exception.getMessage());
    }
}
