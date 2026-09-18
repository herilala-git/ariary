package com.ariary.reference;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PortfolioRepository extends JpaRepository<Portfolio, String> {

	@Query("select p from Portfolio p join fetch p.entity order by p.code")
	List<Portfolio> findAllWithEntity();

}
