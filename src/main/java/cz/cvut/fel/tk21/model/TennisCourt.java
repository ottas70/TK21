package cz.cvut.fel.tk21.model;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class TennisCourt extends AbstractEntity{

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean availableInSummer;

    @Column(nullable = false)
    private boolean availableInWinter;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SurfaceType surfaceType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Club club;

    @OneToMany(mappedBy = "tennisCourt", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private Collection<Reservation> reservations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAvailableInSummer() {
        return availableInSummer;
    }

    public void setAvailableInSummer(boolean availableInSummer) {
        this.availableInSummer = availableInSummer;
    }

    public boolean isAvailableInWinter() {
        return availableInWinter;
    }

    public void setAvailableInWinter(boolean availableInWinter) {
        this.availableInWinter = availableInWinter;
    }

    public SurfaceType getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(SurfaceType surfaceType) {
        this.surfaceType = surfaceType;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Collection<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Collection<Reservation> reservations) {
        this.reservations = reservations;
    }
}
