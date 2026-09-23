package com.ariary.operation;

public enum Status {

	NEW, VALIDATED, CONFIRMED, SETTLED, CANCELLED;

	public boolean canMoveTo(Status target) {
		return switch (this) {
		case NEW -> target == VALIDATED || target == CANCELLED;
		case VALIDATED -> target == CONFIRMED || target == CANCELLED;
		case CONFIRMED -> target == SETTLED || target == CANCELLED;
		case SETTLED, CANCELLED -> false;
		};
	}

	public Status moveTo(Status target) {
		if (!canMoveTo(target)) {
			throw new IllegalStateException("Cannot move from " + this + " to " + target);
		}
		return target;
	}

}
