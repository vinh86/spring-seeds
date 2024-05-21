package com.example.demo.mapping6_abstractClass;

public class TransactionDTO {
    private String uuid;
    private Long totalInCents;
    
    
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Long getTotalInCents() {
		return totalInCents;
	}
	public void setTotalInCents(Long totalInCents) {
		this.totalInCents = totalInCents;
	}

}
