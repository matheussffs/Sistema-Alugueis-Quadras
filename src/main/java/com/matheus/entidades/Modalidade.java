package com.matheus.entidades;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
*
* @author Matheus Fassicollo
*/
@Entity
@Table(name = "modalidades")
public class Modalidade implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mod_id")
    private Integer modId;

    @Column(name = "mod_nome", length = 50, nullable = false, unique = true)
    private String modNome;


    public Modalidade() {
    }


	public Integer getModId() {
		return modId;
	}


	public void setModId(Integer modId) {
		this.modId = modId;
	}


	public String getModNome() {
		return modNome;
	}


	public void setModNome(String modNome) {
		this.modNome = modNome;
	}


	@Override
	public int hashCode() {
		return Objects.hash(modId);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Modalidade other = (Modalidade) obj;
		return Objects.equals(modId, other.modId);
	}
}
