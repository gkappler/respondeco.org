package org.respondeco.respondeco.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Entity;
import java.io.Serializable;

/**
 * SocialMediaConnection Object
 * Represents a connection to a social media platform for a user
 * With help of this object it is possible to obtain a connection object for API interaction.
 */
@Entity
@Data
@Table(name = "T_SOCIALMEDIA")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SocialMediaConnection extends AbstractAuditingEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String provider;

    private String token;

    private String secret;

}
