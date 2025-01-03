package io.github.alanabarbosa.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlRootElement;
@XmlRootElement
@Entity
@Table(name = "files")
public class File implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    
    @JsonProperty("content_type")
    private String contentType;

    @Lob
    private byte[] data;   
    

	public File() {
		super();
	}

	public File(Long id, String filename, byte[] data, String contentType) {
		super();
		this.id = id;
		this.filename = filename;
		this.data = data;
		this.contentType = contentType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		result = prime * result + Objects.hash(contentType, filename, id);
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
		File other = (File) obj;
		return Objects.equals(contentType, other.contentType) && Arrays.equals(data, other.data)
				&& Objects.equals(filename, other.filename) && Objects.equals(id, other.id);
	}

    
}
