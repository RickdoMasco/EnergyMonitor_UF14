import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name = "role")
@NoArgsConstructor 
@Getter 
@Setter 
public class RoleEntity {
    
    @Id 
    @Column (name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)// la gestione dell'autoincrement viene affidata al DB
    private Long id;
    
    @Column (name = "name", unique = true)
    @NotNull 
    private String name; 
    
    public RoleEntity (String name){
        this.name = name; 
    }
}
