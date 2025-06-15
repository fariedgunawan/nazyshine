package com.nazyshine.controller;

import com.nazyshine.model.Booking;
import com.nazyshine.model.BookingStatus;
import com.nazyshine.model.Service;
import com.nazyshine.model.User;
import org.springframework.http.HttpStatus; // PENTING: Pastikan ini diimpor di sini
import com.nazyshine.payload.request.BookingRequest;
import com.nazyshine.payload.response.MessageResponse;
import com.nazyshine.payload.response.CalculationResponse; // <-- IMPORT INI
import com.nazyshine.repository.BookingRepository;
import com.nazyshine.repository.ServiceRepository;
import com.nazyshine.repository.UserRepository;
import com.nazyshine.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class UserController {

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;

    // Get All Services (User can view)
    @GetMapping("/services")
    public ResponseEntity<List<Service>> getAllServicesForUser() {
        List<Service> services = serviceRepository.findAll();
        return ResponseEntity.ok(services);
    }

    // Calculate Total Price for Selected Services
    @PostMapping("/calculate-total")
    public ResponseEntity<?> calculateTotalPrice(@RequestBody Set<Long> serviceIds) {
        if (serviceIds == null || serviceIds.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Please provide service IDs."));
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<String> selectedServicesNames = new ArrayList<>();

        for (Long id : serviceIds) {
            Optional<Service> serviceOptional = serviceRepository.findById(id);
            if (serviceOptional.isPresent()) {
                Service service = serviceOptional.get();
                totalPrice = totalPrice.add(service.getPrice());
                selectedServicesNames.add(service.getName());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Service with ID " + id + " not found."));
            }
        }

        String servicesListString = String.join(", ", selectedServicesNames);

        CalculationResponse response = new CalculationResponse(
                String.format("Total price for selected services (%s): %.2f", servicesListString, totalPrice),
                totalPrice,
                selectedServicesNames
        );

        return ResponseEntity.ok(response);
    }

    // --- NEW BOOKING ENDPOINTS ---

    // Create a new Booking
    @PostMapping("/bookings")
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequest bookingRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        Set<Service> selectedServices = bookingRequest.getServiceIds().stream()
                .map(serviceId -> serviceRepository.findById(serviceId)
                        .orElseThrow(() -> new RuntimeException("Error: Service not found with id " + serviceId)))
                .collect(Collectors.toSet());

        if (selectedServices.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Please select at least one service."));
        }

        BigDecimal calculatedTotalPrice = selectedServices.stream()
                .map(Service::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Booking newBooking = new Booking(currentUser, bookingRequest.getBookingTime(), BookingStatus.PENDING);
        newBooking.setServices(selectedServices);
        newBooking.setTotalPrice(calculatedTotalPrice);

        bookingRepository.save(newBooking);

        return ResponseEntity.ok(new MessageResponse("Booking created successfully with total price: " + calculatedTotalPrice));
    }

    // Get Bookings for Current User
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getUserBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        List<Booking> userBookings = bookingRepository.findByUser(currentUser);
        return ResponseEntity.ok(userBookings);
    }
}