package org.launchcode.VetConnect.controllers;

import org.launchcode.VetConnect.models.Claim;
import org.launchcode.VetConnect.models.Request;
import org.launchcode.VetConnect.models.User;
import org.launchcode.VetConnect.models.data.ClaimRepository;
import org.launchcode.VetConnect.models.data.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController extends VetConnectController{

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    ClaimRepository claimRepository;

    @GetMapping(value="dashboard")
    public String getDashboard(Model model, HttpServletRequest request) {
        User user = getUserFromSession(request.getSession(false));

        if (user.getUserType().equals("petOwner")) {
            return "redirect:dashboard-pet-owner";
        }
        else if (user.getUserType().equals("vet")) {
                return "redirect:dashboard-vet";
        }
        else {
            return "redirect:error";
        }
    }

    @GetMapping(value="dashboard-pet-owner")
    public String displayDashboardPetOwner(Model model, HttpServletRequest request, @RequestParam(required = false) String filter) {
        User this_user = getUserFromSession(request.getSession(false));
        if (!this_user.getUserType().equals("petOwner")) {
            return "redirect:error";
        }

        List<Request> filteredRequests = new ArrayList<>();

        if(filter == null || filter == "all") {
            filteredRequests = requestRepository.findByUserId(this_user.getId());

        } else {
            filteredRequests = requestRepository.findByUserIdAndStatus(this_user.getId(), filter);
        }
        model.addAttribute("requests", filteredRequests);
        model.addAttribute("filter", filter);
        return "dashboard-pet-owner";
    }

    @GetMapping(value="dashboard-vet")
    public String displayDashboardVet(Model model, HttpServletRequest request) {
        User this_user = getUserFromSession(request.getSession(false));
        if (!this_user.getUserType().equals("vet")) {
            return "redirect:error";
        }

        model.addAttribute("approvedClaims", this_user.getApprovedClaims());
        model.addAttribute("claims", this_user.getClaims());
        model.addAttribute("requests", this_user.getRequests());
        return "dashboard-vet";
    }

}
