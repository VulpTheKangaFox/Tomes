package com.vulp.tomes.init;

import com.vulp.tomes.inventory.container.WitchMerchantContainer;
import net.minecraft.inventory.container.ContainerType;

public class ContainerInit {

    public static ContainerType<WitchMerchantContainer> witch_merchant_container = new ContainerType<>(WitchMerchantContainer::new);

}
