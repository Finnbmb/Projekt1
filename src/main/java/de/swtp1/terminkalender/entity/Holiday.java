package de.swtp1.terminkalender.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * JPA Entity für Feiertage
 * Unterstützt bundeslandspezifische Feiertage
 */
@Entity
@Table(name = "holidays")
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name ist erforderlich")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Datum ist erforderlich")
    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "holiday_type")
    private HolidayType type = HolidayType.PUBLIC;

    @Column(name = "federal_state")
    private String federalState; // null = bundesweit

    @Column(name = "is_recurring")
    private boolean isRecurring = true;

    @Column(name = "description")
    private String description;

    // Konstruktoren
    public Holiday() {
    }

    public Holiday(String name, LocalDate date, HolidayType type, String federalState) {
        this.name = name;
        this.date = date;
        this.type = type;
        this.federalState = federalState;
    }

    // Enum für Feiertagstypen
    public enum HolidayType {
        PUBLIC("Gesetzlicher Feiertag"),
        RELIGIOUS("Religiöser Feiertag"),
        CULTURAL("Kultureller Feiertag"),
        SCHOOL("Schulferien"),
        MEMORIAL("Gedenktag");

        private final String displayName;

        HolidayType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Enum für Bundesländer
    public enum FederalState {
        BW("Baden-Württemberg"),
        BY("Bayern"),
        BE("Berlin"),
        BB("Brandenburg"),
        HB("Bremen"),
        HH("Hamburg"),
        HE("Hessen"),
        MV("Mecklenburg-Vorpommern"),
        NI("Niedersachsen"),
        NW("Nordrhein-Westfalen"),
        RP("Rheinland-Pfalz"),
        SL("Saarland"),
        SN("Sachsen"),
        ST("Sachsen-Anhalt"),
        SH("Schleswig-Holstein"),
        TH("Thüringen");

        private final String displayName;

        FederalState(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Getter und Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public HolidayType getType() {
        return type;
    }

    public void setType(HolidayType type) {
        this.type = type;
    }

    public String getFederalState() {
        return federalState;
    }

    public void setFederalState(String federalState) {
        this.federalState = federalState;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Holiday{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", type=" + type +
                ", federalState='" + federalState + '\'' +
                '}';
    }
}
