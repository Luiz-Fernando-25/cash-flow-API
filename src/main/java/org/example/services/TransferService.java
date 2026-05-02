package org.example.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.example.domain.models.Transfer;

public interface TransferService {
  Transfer create(
    BigDecimal value,
    Date date,
    Integer accOutputId,
    Integer accInputId
  );

  Transfer findById(Integer transferId);

  Transfer update(Integer transferId, BigDecimal value);

  List<Transfer> listAll();

  void remove(Integer transferId);
}
