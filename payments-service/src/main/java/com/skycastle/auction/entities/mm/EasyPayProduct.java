package com.skycastle.auction.entities.mm;

import com.skycastle.auction.utils.Utils;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@DiscriminatorValue(value= Utils.PROVIDER.Values.EASYPAY)
public class EasyPayProduct extends  MobileMoneyProduct{
}
