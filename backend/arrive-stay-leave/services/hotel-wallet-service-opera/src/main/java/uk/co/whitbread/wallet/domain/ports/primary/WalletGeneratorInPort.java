package uk.co.whitbread.wallet.domain.ports.primary;


import uk.co.whitbread.wallet.domain.model.in.WalletRequest;

public interface WalletGeneratorInPort {

  byte[] generateWalletPass(WalletRequest walletRequest);
}
