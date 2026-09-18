package com.ariary.reference;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessDateRepository extends JpaRepository<BusinessDate, String> {

	default BusinessDate current() {
		return findById("Y").orElseThrow();
	}

}
