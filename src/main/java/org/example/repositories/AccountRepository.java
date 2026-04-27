package org.example.repositories;

import org.example.domain.models.AbstractAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository
  extends JpaRepository<AbstractAccount, Integer> {}
