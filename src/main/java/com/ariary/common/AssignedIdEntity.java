package com.ariary.common;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Transient;

@MappedSuperclass
public abstract class AssignedIdEntity<ID> implements Persistable<ID> {

	@Transient
	// sinon save() fait un select avant chaque insert
	private boolean isNew = true;

	@Override
	public boolean isNew() {
		return isNew;
	}

	@PostLoad
	@PostPersist
	void markNotNew() {
		isNew = false;
	}

}
