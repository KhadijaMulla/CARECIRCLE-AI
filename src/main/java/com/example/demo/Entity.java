package com.example.demo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity.java — Complete JPA Domain Model for CareCircle AI
 * 
 * Strict Single-File Domain Model: Contains all Entities and Enums as package-private classes,
 * ensuring high cohesion and compliance with the 4-Java-file architectural rule.
 */
public class Entity {
    // Top-level public placeholder and domain constants
    public static final String ROLE_ELDERLY = "ELDERLY";
    public static final String ROLE_FAMILY = "FAMILY";
    public static final String ROLE_CARETAKER = "CARETAKER";
}

/* ==========================================================================
   DOMAIN ENUMS
   ========================================================================== */

enum Role {
    ELDERLY,
    FAMILY,
    CARETAKER
}

enum DoseStatus {
    PENDING,
    TAKEN,
    SNOOZED,
    SKIPPED,
    UNCONFIRMED
}

enum InvitationStatus {
    ACTIVE,
    ACCEPTED,
    EXPIRED,
    REVOKED
}

enum InsightSeverity {
    INFO,
    WARNING,
    URGENT,
    POSITIVE
}

enum NotificationCategory {
    MEDICATION,
    APPOINTMENT,
    CAREGAP,
    FAMILY,
    SYSTEM
}

/* ==========================================================================
   1. USER ENTITY
   ========================================================================== */

@jakarta.persistence.Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email", name = "uk_user_email")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Size(max = 120)
    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @JsonIgnore
    @NotBlank(message = "Password hash is required")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "date_of_birth", length = 50)
    private String dateOfBirth;

    @Column(name = "blood_group", length = 10)
    private String bloodGroup;

    @Column(name = "chronic_conditions", length = 500)
    private String chronicConditions;

    @Column(name = "emergency_contact", length = 255)
    private String emergencyContact;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User() {}

    public User(String fullName, String email, String passwordHash, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getChronicConditions() { return chronicConditions; }
    public void setChronicConditions(String chronicConditions) { this.chronicConditions = chronicConditions; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

/* ==========================================================================
   2. MEDICATION ENTITY
   ========================================================================== */

@jakarta.persistence.Entity
@Table(name = "medications", indexes = {
    @Index(name = "idx_med_patient", columnList = "patient_id"),
    @Index(name = "idx_med_active", columnList = "active")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnore
    private User patient;

    @NotBlank(message = "Medication name is required")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Dosage is required")
    @Column(name = "dosage", nullable = false, length = 50)
    private String dosage;

    @Column(name = "form", length = 50)
    private String form = "Tablet";

    @NotBlank(message = "Frequency is required")
    @Column(name = "frequency", nullable = false, length = 50)
    private String frequency;

    @NotBlank(message = "Scheduled time is required")
    @Column(name = "scheduled_time", nullable = false, length = 20)
    private String scheduledTime;

    @Column(name = "timing_tag", length = 50)
    private String timingTag = "With Lunch";

    @Column(name = "instructions", length = 500)
    private String instructions;

    @Column(name = "doctor", length = 100)
    private String doctor;

    @Column(name = "remaining_refills")
    private int remainingRefills = 3;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Medication() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.startDate == null) {
            this.startDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getPatient() { return patient; }
    public void setPatient(User patient) { this.patient = patient; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getForm() { return form; }
    public void setForm(String form) { this.form = form; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getTimingTag() { return timingTag; }
    public void setTimingTag(String timingTag) { this.timingTag = timingTag; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }

    public int getRemainingRefills() { return remainingRefills; }
    public void setRemainingRefills(int remainingRefills) { this.remainingRefills = remainingRefills; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

/* ==========================================================================
   3. DOSE RECORD ENTITY
   ========================================================================== */

@jakarta.persistence.Entity
@Table(name = "dose_records", indexes = {
    @Index(name = "idx_dose_patient_date", columnList = "patient_id, scheduled_date"),
    @Index(name = "idx_dose_status", columnList = "status")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
class DoseRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnore
    private User patient;

    @NotNull
    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @NotBlank
    @Column(name = "scheduled_time", nullable = false, length = 20)
    private String scheduledTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DoseStatus status = DoseStatus.PENDING;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "skip_reason", length = 255)
    private String skipReason;

    @Column(name = "snooze_minutes")
    private Integer snoozeMinutes;

    @Column(name = "notes", length = 255)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public DoseRecord() {}

    public DoseRecord(Medication medication, User patient, LocalDate scheduledDate, String scheduledTime) {
        this.medication = medication;
        this.patient = patient;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.status = DoseStatus.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Medication getMedication() { return medication; }
    public void setMedication(Medication medication) { this.medication = medication; }

    public User getPatient() { return patient; }
    public void setPatient(User patient) { this.patient = patient; }

    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    public DoseStatus getStatus() { return status; }
    public void setStatus(DoseStatus status) { this.status = status; }

    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }

    public String getSkipReason() { return skipReason; }
    public void setSkipReason(String skipReason) { this.skipReason = skipReason; }

    public Integer getSnoozeMinutes() { return snoozeMinutes; }
    public void setSnoozeMinutes(Integer snoozeMinutes) { this.snoozeMinutes = snoozeMinutes; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

/* ==========================================================================
   4. APPOINTMENT ENTITY
   ========================================================================== */

@jakarta.persistence.Entity
@Table(name = "appointments", indexes = {
    @Index(name = "idx_app_patient", columnList = "patient_id"),
    @Index(name = "idx_app_date", columnList = "appointment_date")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnore
    private User patient;

    @NotBlank(message = "Doctor name is required")
    @Column(name = "doctor", nullable = false, length = 100)
    private String doctor;

    @NotBlank(message = "Specialty is required")
    @Column(name = "specialty", nullable = false, length = 100)
    private String specialty;

    @NotBlank(message = "Location is required")
    @Column(name = "location", nullable = false, length = 200)
    private String location;

    @NotNull(message = "Appointment date is required")
    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @NotBlank(message = "Appointment time is required")
    @Column(name = "appointment_time", nullable = false, length = 20)
    private String appointmentTime;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "escort", length = 150)
    private String escort;

    @Column(name = "cancelled", nullable = false)
    private boolean cancelled = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Appointment() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getPatient() { return patient; }
    public void setPatient(User patient) { this.patient = patient; }

    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getEscort() { return escort; }
    public void setEscort(String escort) { this.escort = escort; }

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

/* ==========================================================================
   5. FAMILY CONNECTION ENTITY
   ========================================================================== */

@jakarta.persistence.Entity
@Table(name = "family_connections", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"elderly_id", "family_member_id"}, name = "uk_elderly_family")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
class FamilyConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elderly_id", nullable = false)
    private User elderly;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_member_id", nullable = false)
    private User familyMember;

    @Column(name = "relationship", length = 50)
    private String relationship = "Family Member";

    @Column(name = "permissions", length = 50)
    private String permissions = "FULL_ACCESS";

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public FamilyConnection() {}

    public FamilyConnection(User elderly, User familyMember, String relationship, String permissions) {
        this.elderly = elderly;
        this.familyMember = familyMember;
        this.relationship = relationship;
        this.permissions = permissions;
        this.active = true;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getElderly() { return elderly; }
    public void setElderly(User elderly) { this.elderly = elderly; }

    public User getFamilyMember() { return familyMember; }
    public void setFamilyMember(User familyMember) { this.familyMember = familyMember; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) { this.permissions = permissions; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

/* ==========================================================================
   6. ACCESS INVITATION ENTITY
   ========================================================================== */

@jakarta.persistence.Entity
@Table(name = "access_invitations", indexes = {
    @Index(name = "idx_invite_code", columnList = "passcode"),
    @Index(name = "idx_invite_elderly", columnList = "elderly_id")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
class AccessInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elderly_id", nullable = false)
    private User elderly;

    @Column(name = "passcode", nullable = false, length = 20)
    private String passcode;

    @JsonIgnore
    @Column(name = "passcode_hash", nullable = false, length = 255)
    private String passcodeHash;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InvitationStatus status = InvitationStatus.ACTIVE;

    @Column(name = "target_relationship", length = 50)
    private String targetRelationship = "Family Member";

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accepted_by_id")
    private User acceptedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AccessInvitation() {}

    public AccessInvitation(User elderly, String passcode, String passcodeHash, String targetRelationship, LocalDateTime expiresAt) {
        this.elderly = elderly;
        this.passcode = passcode;
        this.passcodeHash = passcodeHash;
        this.targetRelationship = targetRelationship;
        this.expiresAt = expiresAt;
        this.status = InvitationStatus.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt) || this.status == InvitationStatus.EXPIRED;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getElderly() { return elderly; }
    public void setElderly(User elderly) { this.elderly = elderly; }

    public String getPasscode() { return passcode; }
    public void setPasscode(String passcode) { this.passcode = passcode; }

    public String getPasscodeHash() { return passcodeHash; }
    public void setPasscodeHash(String passcodeHash) { this.passcodeHash = passcodeHash; }

    public InvitationStatus getStatus() { return status; }
    public void setStatus(InvitationStatus status) { this.status = status; }

    public String getTargetRelationship() { return targetRelationship; }
    public void setTargetRelationship(String targetRelationship) { this.targetRelationship = targetRelationship; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public User getAcceptedBy() { return acceptedBy; }
    public void setAcceptedBy(User acceptedBy) { this.acceptedBy = acceptedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}

/* ==========================================================================
   7. NOTIFICATION ENTITY
   ========================================================================== */

@jakarta.persistence.Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notif_recipient", columnList = "recipient_id"),
    @Index(name = "idx_notif_unread", columnList = "recipient_id, is_read")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    @JsonIgnore
    private User recipient;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private NotificationCategory category = NotificationCategory.SYSTEM;

    @NotBlank
    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @NotBlank
    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "type", length = 30)
    private String type = "info";

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(User recipient, NotificationCategory category, String title, String message, String type) {
        this.recipient = recipient;
        this.category = category;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = false;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }

    public NotificationCategory getCategory() { return category; }
    public void setCategory(NotificationCategory category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}

/* ==========================================================================
   8. CARE INSIGHT ENTITY (CareGap AI Signature Model)
   ========================================================================== */

@jakarta.persistence.Entity
@Table(name = "care_insights", indexes = {
    @Index(name = "idx_insight_patient", columnList = "patient_id"),
    @Index(name = "idx_insight_severity", columnList = "severity")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
class CareInsight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnore
    private User patient;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private InsightSeverity severity = InsightSeverity.INFO;

    @NotBlank
    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @NotBlank
    @Column(name = "analysis", nullable = false, length = 1000)
    private String analysis;

    @NotBlank
    @Column(name = "recommendation", nullable = false, length = 500)
    private String recommendation;

    @Column(name = "metric", length = 50)
    private String metric;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public CareInsight() {}

    public CareInsight(User patient, InsightSeverity severity, String title, String analysis, String recommendation, String metric) {
        this.patient = patient;
        this.severity = severity;
        this.title = title;
        this.analysis = analysis;
        this.recommendation = recommendation;
        this.metric = metric;
        this.active = true;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getPatient() { return patient; }
    public void setPatient(User patient) { this.patient = patient; }

    public InsightSeverity getSeverity() { return severity; }
    public void setSeverity(InsightSeverity severity) { this.severity = severity; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAnalysis() { return analysis; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public String getMetric() { return metric; }
    public void setMetric(String metric) { this.metric = metric; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
