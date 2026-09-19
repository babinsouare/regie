package com.api.regie.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "logs")
public class Logs {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "dt_created")
    private Date dtCreated;

    @Column(name = "request",columnDefinition = "TEXT")
    @JsonProperty()
    private String request;

    @Column(name = "response",columnDefinition = "TEXT")
    @JsonProperty()
    private String response;

    @Column(name = "msisdn")
    @JsonProperty()
    private String msisdn;

    @Column(name = "reference")
    @JsonProperty()
    private String reference;

    @Column(name = "message")
    @JsonProperty()
    private String message;


}
