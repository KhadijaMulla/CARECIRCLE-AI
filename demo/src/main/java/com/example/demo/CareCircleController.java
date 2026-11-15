package com.example.demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * CareCircleController.java — Complete REST API Controller & Global Exception Handling
 * 
 * Strict Single-File Controller Architecture:
 * - Direct mapping of all 14 frontend functional views
 * - Unified ApiResponse wrapper: { success, message, data, errors }
 * - Centralized RestControllerAdvice Exception Handling
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CareCircleController {

    private static final Logger log = LoggerFactory.getLogger(CareCircleController.class);
    private final CareCircleService careCircleService;

    public CareCircleController(CareCircleService careCircleService) {
        this.careCircleService = careCircleService;
    }

    /* ==========================================================================
       1. AUTHENTICATION & PROFILE ENDPOINTS
       ========================================================================== */

    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req) {
        AuthResponse res = careCircleService.registerUser(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User registered successfully", res));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {
        AuthResponse res = careCircleService.loginUser(req);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", res));
    }

    @GetMapping({"/auth/me", "/users/me"})
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Current user retrieved", careCircleService.getProfile(user)));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile() {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Profile details retrieved", careCircleService.getProfile(user)));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@Valid @RequestBody ProfileUpdateRequest req) {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", careCircleService.updateProfile(user, req)));
    }

    /* ==========================================================================
       2. MEDICATION MANAGEMENT ENDPOINTS
       ========================================================================== */

    @GetMapping("/medications")
    public ResponseEntity<ApiResponse<List<MedicationResponse>>> getMedications() {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Medications retrieved successfully", careCircleService.getMedications(user)));
    }

    @PostMapping("/medications")
    public ResponseEntity<ApiResponse<MedicationResponse>> addMedication(@Valid @RequestBody MedicationRequest req) {
        User user = careCircleService.getCurrentAuthenticatedUser();
        MedicationResponse created = careCircleService.createMedication(req, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Medication registered successfully", created));
    }

    @GetMapping("/medications/{id}")
    public ResponseEntity<ApiResponse<MedicationResponse>> getMedicationById(@PathVariable Long id) {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Medication details retrieved", careCircleService.getMedicationById(id, user)));
    }

    @PutMapping("/medications/{id}")
    public ResponseEntity<ApiResponse<MedicationResponse>> updateMedication(
            @PathVariable Long id,
            @Valid @RequestBody MedicationRequest req) {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Medication updated successfully", careCircleService.updateMedication(id, req, user)));
    }

    @DeleteMapping("/medications/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMedication(@PathVariable Long id) {
        User user = careCircleService.getCurrentAuthenticatedUser();
        careCircleService.deleteMedication(id, user);
        return ResponseEntity.ok(ApiResponse.ok("Medication deleted successfully", null));
    }

    /* ==========================================================================
       3. DOSE TRACKING & ADHERENCE ENDPOINTS
       ========================================================================== */

    @GetMapping("/doses/today")
    public ResponseEntity<ApiResponse<List<DoseResponse>>> getTodayDoses() {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Today's dose schedule retrieved", careCircleService.getTodayDoses(user)));
    }

    @PostMapping("/doses/{id}/confirm")
    public ResponseEntity<ApiResponse<DoseResponse>> confirmDose(
            @PathVariable Long id,
            @RequestBody(required = false) DoseConfirmRequest req) {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Dose confirmed taken", careCircleService.confirmDose(id, req, user)));
    }

    @PostMapping("/doses/{id}/skip")
    public ResponseEntity<ApiResponse<DoseResponse>> skipDose(
            @PathVariable Long id,
            @RequestBody(required = false) DoseSkipRequest req) {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Dose skipped", careCircleService.skipDose(id, req, user)));
    }

    @PostMapping("/doses/{id}/snooze")
    public ResponseEntity<ApiResponse<DoseResponse>> snoozeDose(
            @PathVariable Long id,
            @RequestBody(required = false) DoseSnoozeRequest req) {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Dose reminder snoozed", careCircleService.snoozeDose(id, req, user)));
    }

    @GetMapping("/doses/history")
    public ResponseEntity<ApiResponse<List<DoseResponse>>> getDoseHistory(
            @RequestParam(defaultValue = "7") int days) {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Dose history retrieved", careCircleService.getDoseHistory(user, days)));
    }

    /* ==========================================================================
       4. APPOINTMENT MANAGEMENT ENDPOINTS
       ========================================================================== */

    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAppointments() {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Appointments retrieved successfully", careCircleService.getAppointments(user)));
    }

    @PostMapping("/appointments")
    public ResponseEntity<ApiResponse<AppointmentResponse>> createAppointment(@Valid @RequestBody AppointmentRequest req) {
        User user = careCircleService.getCurrentAuthenticatedUser();
        AppointmentResponse created = careCircleService.createAppointment(req, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Appointment scheduled successfully", created));
    }

    @PutMapping("/appointments/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequest req) {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Appointment updated successfully", careCircleService.updateAppointment(id, req, user)));
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAppointment(@PathVariable Long id) {
        User user = careCircleService.getCurrentAuthenticatedUser();
        careCircleService.deleteAppointment(id, user);
        return ResponseEntity.ok(ApiResponse.ok("Appointment cancelled successfully", null));
    }

    /* ==========================================================================
       5. FAMILY CIRCLE & PERMISSION SHARING ENDPOINTS
       ========================================================================== */

    @PostMapping("/family/invitations")
    public ResponseEntity<ApiResponse<InvitationResponse>> createInvitation(@RequestBody(required = false) Map<String, String> req) {
        User user = careCircleService.getCurrentAuthenticatedUser();
        String relationship = (req != null && req.get("relationship") != null) ? req.get("relationship") : "Family";
        InvitationResponse res = careCircleService.createInvitation(user, relationship);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Family invitation generated successfully", res));
    }

    @PostMapping("/family/join")
    public ResponseEntity<ApiResponse<FamilyConnectionResponse>> joinFamily(@Valid @RequestBody JoinCircleRequest req) {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Successfully joined CareCircle", careCircleService.joinFamilyCircle(user, req)));
    }

    @GetMapping("/family/connections")
    public ResponseEntity<ApiResponse<List<FamilyConnectionResponse>>> getFamilyMembers() {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Family circle members retrieved", careCircleService.getFamilyConnections(user)));
    }

    @PutMapping("/family/permissions")
    public ResponseEntity<ApiResponse<Void>> updatePermissions(@RequestBody Map<String, Object> req) {
        User user = careCircleService.getCurrentAuthenticatedUser();
        Long connectionId = Long.valueOf(req.get("connectionId").toString());
        String permissions = req.get("permissions") != null ? req.get("permissions").toString() : "FULL";
        careCircleService.updateFamilyPermissions(connectionId, permissions, user);
        return ResponseEntity.ok(ApiResponse.ok("Circle member permissions updated", null));
    }

    @DeleteMapping("/family/connections/{id}")
    public ResponseEntity<ApiResponse<Void>> removeFamilyConnection(@PathVariable Long id) {
        User user = careCircleService.getCurrentAuthenticatedUser();
        careCircleService.revokeFamilyConnection(id, user);
        return ResponseEntity.ok(ApiResponse.ok("Connection removed successfully", null));
    }

    /* ==========================================================================
       6. ROLE-SPECIFIC DASHBOARD ENDPOINTS
       ========================================================================== */

    @GetMapping("/dashboard/elderly")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getElderlyDashboard() {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Senior Citizen dashboard loaded", careCircleService.getElderlyDashboard(user)));
    }

    @GetMapping("/dashboard/family")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFamilyDashboard() {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Family Observer dashboard loaded", careCircleService.getFamilyDashboard(user)));
    }

    @GetMapping("/dashboard/caretaker")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCaretakerDashboard() {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Professional Caregiver dashboard loaded", careCircleService.getCaretakerDashboard(user)));
    }

    @GetMapping("/dashboard/adherence")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdherenceStats() {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Adherence statistics calculated", careCircleService.getAdherenceStats(user)));
    }

    /**
     * Unified Dashboard Route — Automatically dynamically dispatches by user role
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Object>> getUnifiedDashboard() {
        User user = careCircleService.getCurrentAuthenticatedUser();
        Object data = switch (user.getRole()) {
            case ELDERLY -> careCircleService.getElderlyDashboard(user);
            case FAMILY -> careCircleService.getFamilyDashboard(user);
            case CARETAKER -> careCircleService.getCaretakerDashboard(user);
        };
        return ResponseEntity.ok(ApiResponse.ok("Dashboard data retrieved for role: " + user.getRole(), data));
    }

    /* ==========================================================================
       7. AI SERVICES & CAREGAP SIGNATURE ENGINE
       ========================================================================== */

    @PostMapping({"/ai/prescription/explain", "/ai/explain-prescription"})
    public ResponseEntity<ApiResponse<PrescriptionExplainResponse>> explainPrescription(
            @Valid @RequestBody PrescriptionExplainRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Prescription simplified by Gemini AI", careCircleService.explainPrescription(req)));
    }

    @PostMapping("/ai/care-insights")
    public ResponseEntity<ApiResponse<List<CareInsightResponse>>> generateCareInsights() {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("CareGap proactive insights evaluated", careCircleService.generateCareInsights(user)));
    }

    @GetMapping("/caregap/insights")
    public ResponseEntity<ApiResponse<List<CareInsightResponse>>> getActiveCareInsights() {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Proactive clinical insights retrieved", careCircleService.getCareInsights(user)));
    }

    /* ==========================================================================
       8. NOTIFICATION ENDPOINTS
       ========================================================================== */

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications() {
        User user = careCircleService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok("Notifications retrieved", careCircleService.getNotifications(user)));
    }

    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markNotificationRead(@PathVariable Long id) {
        User user = careCircleService.getCurrentAuthenticatedUser();
        careCircleService.markNotificationRead(id, user);
        return ResponseEntity.ok(ApiResponse.ok("Notification marked as read", null));
    }

    @PutMapping("/notifications/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllNotificationsRead() {
        User user = careCircleService.getCurrentAuthenticatedUser();
        careCircleService.markAllNotificationsRead(user);
        return ResponseEntity.ok(ApiResponse.ok("All notifications marked as read", null));
    }
}

/* ==========================================================================
   GENERIC API RESPONSE ENVELOPE RECORD
   ========================================================================== */

@JsonInclude(JsonInclude.Include.NON_NULL)
record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        List<String> errors
) {
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        return new ApiResponse<>(false, message, null, errors != null ? errors : List.of(message));
    }
}

/* ==========================================================================
   CENTRALIZED GLOBAL EXCEPTION ADVICE
   ========================================================================== */

@RestControllerAdvice
class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.add(fe.getField() + ": " + fe.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation error", errors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), List.of(ex.getMessage())));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), List.of(ex.getMessage())));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<Void>> handleSecurity(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage(), List.of(ex.getMessage())));
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFound(org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        log.error("[CareCircle Exception] Unhandled error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error: " + ex.getMessage(), List.of(ex.getMessage())));
    }
}

@org.springframework.context.annotation.Configuration
class WebMvcConfig implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {
    @Override
    public void addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/api/index.html").setViewName("forward:/index.html");
    }
}
