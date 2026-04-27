package org.example.repositories;

import org.example.domain.models.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditCardRepository
  extends JpaRepository<CreditCard, Integer> {}
