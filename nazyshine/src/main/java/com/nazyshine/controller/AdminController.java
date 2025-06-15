package com.nazyshine.controller;

import com.nazyshine.model.Service;
import com.nazyshine.model.Booking; // Import Booking
import com.nazyshine.model.BookingStatus; // Import BookingStatus
import com.nazyshine.payload.request.ServiceRequest;
import com.nazyshine.payload.response.MessageResponse;
import com.nazyshine.repository.ServiceRepository;
import com.nazyshine.repository.BookingRepository; // Inject BookingRepository
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    BookingRepository bookingRepository; // Inject BookingRepository

    // Create Service
    @PostMapping("/services")
    public ResponseEntity<?> createService(@Valid @RequestBody ServiceRequest serviceRequest) {
        Service newService = new Service();
        newService.setName(serviceRequest.getName());
        newService.setPrice(serviceRequest.getPrice());
        newService.setDescription(serviceRequest.getDescription());

        serviceRepository.save(newService);
        return ResponseEntity.ok(new MessageResponse("Service created successfully!"));
    }

    // Get All Services
    @GetMapping("/services")
    public ResponseEntity<List<Service>> getAllServices() {
        List<Service> services = serviceRepository.findAll();
        return ResponseEntity.ok(services);
    }

    // Get Service by ID
    @GetMapping("/services/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable Long id) {
        return serviceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update Service
    @PutMapping("/services/{id}")
    public ResponseEntity<?> updateService(@PathVariable Long id, @Valid @RequestBody ServiceRequest serviceRequest) {
        return serviceRepository.findById(id)
                .map(service -> {
                    service.setName(serviceRequest.getName());
                    service.setPrice(serviceRequest.getPrice());
                    service.setDescription(serviceRequest.getDescription());
                    serviceRepository.save(service);
                    return ResponseEntity.ok(new MessageResponse("Service updated successfully!"));
                }).orElse(ResponseEntity.notFound().build());
    }

    // Delete Service
    @DeleteMapping("/services/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Long id) {
        if (serviceRepository.existsById(id)) {
            serviceRepository.deleteById(id);
            return ResponseEntity.ok(new MessageResponse("Service deleted successfully!"));
        }
        return ResponseEntity.notFound().build();
    }

    // --- NEW BOOKING MANAGEMENT ENDPOINTS ---

    // Get All Bookings (for Admin to view all bookings)
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        System.out.println("Ditemukan " + bookings.size() + " booking."); 
        return ResponseEntity.ok(bookings);
    }

    // Get Booking by ID
    @GetMapping("/bookings/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return bookingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update Booking Status (e.g., CONFIRM, CANCEL)
    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Long id, @RequestParam String status) {
        return bookingRepository.findById(id)
                .map(booking -> {
                    try {
                        BookingStatus newStatus = BookingStatus.valueOf(status.toUpperCase());
                        booking.setStatus(newStatus);
                        bookingRepository.save(booking);
                        return ResponseEntity.ok(new MessageResponse("Booking status updated to " + newStatus));
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body(new MessageResponse("Invalid booking status: " + status));
                    }
                }).orElse(ResponseEntity.notFound().build());
    }

    // Delete Booking (Admin can delete any booking)
    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        if (bookingRepository.existsById(id)) {
            bookingRepository.deleteById(id);
            return ResponseEntity.ok(new MessageResponse("Booking deleted successfully!"));
        }
        return ResponseEntity.notFound().build();
    }
}