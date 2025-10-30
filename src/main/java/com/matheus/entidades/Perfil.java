package com.matheus.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Matheus Fassicollo
 */

@Entity
@Table(name = "perfis")
public class Perfil implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "perf_id")
	private Integer perfId;
	@Column(name = "perf_nome", length = 100, nullable = false, unique = true)
	private String perfDesc;

	public Perfil() {
	}

	public Integer getPerfId() {
		return perfId;
	}

	public void setPerfId(Integer perfId) {
		this.perfId = perfId;
	}

	public String getPerfDesc() {
		return perfDesc;
	}

	public void setPerfDesc(String perfDesc) {
		this.perfDesc = perfDesc;
	}

	@Override
	public int hashCode() {
		return Objects.hash(perfId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Perfil other = (Perfil) obj;
		return Objects.equals(perfId, other.perfId);
	}
}
