package com.ariary.market;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CurvePointRepository extends JpaRepository<CurvePoint, CurvePoint.Key> {

	List<CurvePoint> findByKeyDateOrderByKeyCurrencyAscKeyDaysAsc(LocalDate date);

	@Modifying
	@Query("delete from CurvePoint p where p.key.date = :date")
	int deleteByDate(LocalDate date);

}
