package com.tsemkalo.experienceApp.enums;

public enum PermissionType {
	READ,
	ADD,
	EDIT,
	DELETE,
	SUBSCRIBE,
	GET_SALARY,
	WRITE_REVIEW,
	DELETE_REVIEW;

	public static class Authorities {
		public static final String READ = "hasAuthority('READ')";
		public static final String ADD = "hasAuthority('ADD')";
		public static final String EDIT = "hasAuthority('EDIT')";
		public static final String DELETE = "hasAuthority('DELETE')";
		public static final String SUBSCRIBE = "hasAuthority('SUBSCRIBE')";
		public static final String GET_SALARY = "hasAuthority('GET_SALARY')";
		public static final String WRITE_REVIEW = "hasAuthority('WRITE_REVIEW')";
		public static final String DELETE_REVIEW = "hasAuthority('DELETE_REVIEW')";
	}
}
