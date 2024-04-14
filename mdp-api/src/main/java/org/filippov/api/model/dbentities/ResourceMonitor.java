package org.filippov.api.model.dbentities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "resource_monitor")
public class ResourceMonitor {
    @Id @GeneratedValue
    @Column(name = "id", length = 20)
    private String id;

//    should be like this in future but for now it's just user login
//    @ManyToOne()
//    @JoinColumn(name = "user_id", nullable = false)
//    @JsonIgnore
//    private User user;

    @Column(name = "user_email", length = 50)
    private String userEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", length = 20)
    private ResourceType resourceType;

    @Getter
    public enum ResourceType {
        ELECTRICITY("electricity"),
        WATER("water"),
        GAS("gas"),
        ;

        private final String name;

        ResourceType(String name) {
            this.name = name;
        }
    }
}
