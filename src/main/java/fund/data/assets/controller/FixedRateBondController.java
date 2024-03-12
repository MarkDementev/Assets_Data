package fund.data.assets.controller;

import fund.data.assets.dto.FixedRateBondDTO;
import fund.data.assets.model.assets.exchange.FixedRateBond;
import fund.data.assets.service.impl.FixedRateBondServiceImpl;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static fund.data.assets.controller.FixedRateBondController.FIXED_RATE_BOND_CONTROLLER_PATH;
import static fund.data.assets.controller.FixedRateBondController.ASSETS_CONTROLLER_PATH;
import static fund.data.assets.controller.FixedRateBondController.EXCHANGE_ASSETS_CONTROLLER_PATH;

@RestController
@RequestMapping("{base_url}" + ASSETS_CONTROLLER_PATH + EXCHANGE_ASSETS_CONTROLLER_PATH
        + FIXED_RATE_BOND_CONTROLLER_PATH)
@AllArgsConstructor
public class FixedRateBondController {
    public static final String ASSETS_CONTROLLER_PATH = "/assets";
    public static final String EXCHANGE_ASSETS_CONTROLLER_PATH = "/exchange";
    public static final String FIXED_RATE_BOND_CONTROLLER_PATH = "/bonds";
    public static final String ID_PATH = "/{id}";
    private final FixedRateBondServiceImpl fixedRateBondService;

    @GetMapping(ID_PATH)
    public FixedRateBond getFixedRateBond(@PathVariable Long id) {
        return fixedRateBondService.getFixedRateBond(id);
    }

    @GetMapping
    public List<FixedRateBond> getFixedRateBonds() {
        return fixedRateBondService.getFixedRateBonds();
    }

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public FixedRateBond createFixedRateBond(@RequestBody @Valid FixedRateBondDTO fixedRateBondDTO) {
//        return fixedRateBondService.createFixedRateBond(fixedRateBondDTO);
//    }

    @DeleteMapping(ID_PATH)
    public void deleteFixedRateBond(@PathVariable Long id) {
        fixedRateBondService.deleteFixedRateBond(id);
    }

    //Продумай, как прописать архитектуру в контроллере и сервисе касаемо того, как именно будет меняться бонд
}
