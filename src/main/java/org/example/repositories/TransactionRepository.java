package org.example.repositories;

import org.example.domain.models.AbstractTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository
  extends JpaRepository<AbstractTransaction, Integer> {}
