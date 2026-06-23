package br.com.midnightsyslabs.flow_control.domain.entity.spent;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpentCategory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Short id;

  private String name;
}
