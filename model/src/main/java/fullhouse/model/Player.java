package fullhouse.model;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Transient;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Cacheable;
import javax.persistence.Basic;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


import java.util.List;
import java.util.Date;

@Entity
@Table(name="player")
public class Player {

	@Id
	@Column(name="id")
	private Integer id;

	@Column(name="first_name")
	private String first_name;

	@Column(name="surname")
	private String surname;

	@Column(name="login")
	private String login;

	@Column(name="total_chips")
	private Integer total_chips;

	public void setId(Integer i){
		this.id = i;
	}

	public Integer getId(){
		return this.id;
	}

	public void setFirst_name(String s){
		this.first_name = s;
	}

	public String getFirst_name(){
		return this.first_name;
	}

	public void setSurname(String s){
		this.surname = s;
	}

	public String getSurname(){
		return this.surname;
	}

	public void setLogin(String s){
		this.login = s;
	}

	public String getLogin(){
		return this.login;
	}

	public void setTotal_chips(Integer i){
		this.total_chips = i;
	}
	
	public Integer getTotal_chips(){
		return this.total_chips;
	}
}
