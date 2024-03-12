package fund.data.assets.controller;

import fund.data.assets.service.impl.FixedRateBondServiceImpl;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static fund.data.assets.controller.FixedRateBondController.FIXED_RATE_BOND_CONTROLLER_PATH;

@RestController
@RequestMapping("{base_url}" + FIXED_RATE_BOND_CONTROLLER_PATH)
@AllArgsConstructor
public class FixedRateBondController {
    public static final String FIXED_RATE_BOND_CONTROLLER_PATH = "/bonds";
    private final FixedRateBondServiceImpl fixedRateBondService;
}
