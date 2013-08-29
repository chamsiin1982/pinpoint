package com.nhn.pinpoint.web.applicationmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.nhn.pinpoint.common.ServiceType;
import com.nhn.pinpoint.common.bo.AgentInfoBo;

/**
 * DB에서 조회한 application호출 관계 정보.
 * 
 * @author netspider
 * 
 */
public class TransactionFlowStatistics {

	private String id;
	private String from;
	private ServiceType fromServiceType;
	private String to;
	private ServiceType toServiceType;

	// key = hostname
	private Map<String, Host> toHostList;
	
	private Set<AgentInfoBo> toAgentSet;

	/**
	 * 
	 * @param hostname
	 *            host이름 또는 endpoint
	 * @param serviceType
	 * @param slot
	 * @param value
	 */
	public void addSample(String hostname, short serviceTypeCode, short slot, long value) {
		// TODO 임시코드
		if (hostname == null || hostname.length() == 0) {
			hostname = "UNKNOWNHOST";
		}

		if (toHostList.containsKey(hostname)) {
			toHostList.get(hostname).getHistogram().addSample(slot, value);
		} else {
			Host host = new Host(hostname, ServiceType.findServiceType(serviceTypeCode), null);
			host.getHistogram().addSample(slot, value);
			toHostList.put(hostname, host);
		}
	}

	public TransactionFlowStatistics(String from, short fromServiceType, String to, short toServiceType) {
		this.from = from;
		this.fromServiceType = ServiceType.findServiceType(fromServiceType);
		this.to = to;
		this.toServiceType = ServiceType.findServiceType(toServiceType);
		this.toHostList = new HashMap<String, Host>();
		this.id = TransactionFlowStatisticsUtils.makeId(this.from, this.fromServiceType, this.to, this.toServiceType);
	}

	public TransactionFlowStatistics(String from, ServiceType fromServiceType, String to, ServiceType toServiceType) {
		this(from, fromServiceType.getCode(), to, toServiceType.getCode());
	}

	public void makeId() {
		this.id = TransactionFlowStatisticsUtils.makeId(from, fromServiceType, to, toServiceType);
	}

	public String getId() {
		return id;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public ServiceType getFromServiceType() {
		return fromServiceType;
	}

	public ServiceType getToServiceType() {
		return toServiceType;
	}

	public void setFrom(String from) {
		this.from = from;
		makeId();
	}

	public void setTo(String to) {
		this.to = to;
		makeId();
	}

	public void setFromServiceType(ServiceType fromServiceType) {
		this.fromServiceType = fromServiceType;
		makeId();
	}

	public void setToServiceType(ServiceType toServiceType) {
		this.toServiceType = toServiceType;
		makeId();
	}

	public Map<String, Host> getToHostList() {
		return toHostList;
	}

	public Set<AgentInfoBo> getToAgentSet() {
		return toAgentSet;
	}

	public void addToAgentSet(Set<AgentInfoBo> agentSet) {
		if (this.toAgentSet != null) {
			this.toAgentSet.addAll(agentSet);
		} else {
			this.toAgentSet = agentSet;
		}
	}

	public TransactionFlowStatistics mergeWith(TransactionFlowStatistics applicationStatistics) {
		if (this.equals(applicationStatistics)) {
			for (Entry<String, Host> entry : applicationStatistics.getToHostList().entrySet()) {
				if (this.toHostList.containsKey(entry.getKey())) {
					this.toHostList.get(entry.getKey()).mergeWith(entry.getValue());
				} else {
					this.toHostList.put(entry.getKey(), entry.getValue());
				}
			}
			return this;
		} else {
			throw new IllegalArgumentException("Can't merge with different link.");
		}
	}

	@Override
	public String toString() {
		return "TransactionFlowStatistics [id=" + id + ", from=" + from + ", fromServiceType=" + fromServiceType + ", to=" + to + ", toServiceType=" + toServiceType + ", toHostList=" + toHostList + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransactionFlowStatistics other = (TransactionFlowStatistics) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
