package fund.data.assets.controller;

import fund.data.assets.dto.owner.ContactDataRussianAssetsOwnerDTO;
import fund.data.assets.dto.owner.PersonalDataRussianAssetsOwnerDTO;
import fund.data.assets.dto.owner.NewRussianAssetsOwnerDTO;
import fund.data.assets.model.asset.owner.RussianAssetsOwner;
import fund.data.assets.service.RussianAssetsOwnerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static fund.data.assets.controller.RussianAssetsOwnerController.RUSSIAN_OWNERS_CONTROLLER_PATH;

/**
 * Контроллер для работы с владельцами активов с гражданством РФ.
 * Обслуживаемая сущность - {@link fund.data.assets.model.asset.owner.RussianAssetsOwner}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@RestController
@RequestMapping("{base-url}" + RUSSIAN_OWNERS_CONTROLLER_PATH)
@AllArgsConstructor
public class RussianAssetsOwnerController {
    public static final String RUSSIAN_OWNERS_CONTROLLER_PATH = "/owners/russia";
    public static final String ID_PATH = "/{id}";
    public static final String PERSONAL_DATA_PATH = "/data";
    public static final String CONTACT_DATA_PATH = "/contacts";
    private final RussianAssetsOwnerService russianAssetsOwnerService;

    @Operation(summary = "Get russian assets owner by id")
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = RussianAssetsOwner.class))
    )
    @GetMapping(ID_PATH)
    public ResponseEntity<RussianAssetsOwner> getRussianAssetsOwner(@PathVariable Long id) {
        return ResponseEntity.ok().body(russianAssetsOwnerService.getRussianAssetsOwner(id));
    }

    @Operation(summary = "Get all russian assets owners")
    @ApiResponses(@ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = RussianAssetsOwner.class)))
    )
    @GetMapping
    public ResponseEntity<List<RussianAssetsOwner>> getRussianAssetsOwners() {
        return ResponseEntity.ok().body(russianAssetsOwnerService.getRussianAssetsOwners());
    }

    @Operation(summary = "Create new russian assets owner")
    @ApiResponse(responseCode = "201", description = "Russian assets owner created")
    @PostMapping
    public ResponseEntity<RussianAssetsOwner> createRussianAssetsOwner(@RequestBody @Valid NewRussianAssetsOwnerDTO
                                                                                   newRussianAssetsOwnerDTO) {
        return ResponseEntity.created(null).body(russianAssetsOwnerService.createRussianAssetsOwner(
                newRussianAssetsOwnerDTO));
    }

    @Operation(summary = "Update personal data of russian assets owner")
    @ApiResponse(responseCode = "200", description = "Russian assets owner personal data updated")
    @PutMapping(ID_PATH + PERSONAL_DATA_PATH)
    public ResponseEntity<RussianAssetsOwner> updateRussianAssetsOwnerPersonalData(@PathVariable Long id, @RequestBody
    @Valid PersonalDataRussianAssetsOwnerDTO personalDataRussianAssetsOwnerDTO) {
        return ResponseEntity.ok().body(russianAssetsOwnerService.updateRussianAssetsOwnerPersonalData(
                id, personalDataRussianAssetsOwnerDTO));
    }

    @Operation(summary = "Update contact data of russian assets owner")
    @ApiResponse(responseCode = "200", description = "Russian assets owner contact data updated")
    @PutMapping(ID_PATH + CONTACT_DATA_PATH)
    public ResponseEntity<RussianAssetsOwner> updateRussianAssetsOwnerContactData(@PathVariable Long id, @RequestBody
    @Valid ContactDataRussianAssetsOwnerDTO contactDataRussianAssetsOwnerDTO) {
        return ResponseEntity.ok().body(russianAssetsOwnerService.updateRussianAssetsOwnerContactData(
                id, contactDataRussianAssetsOwnerDTO));
    }

    @Operation(summary = "Delete russian assets owner")
    @ApiResponse(responseCode = "200", description = "Russian assets owner deleted")
    @DeleteMapping(ID_PATH)
    public void deleteRussianAssetsOwner(@PathVariable Long id) {
        russianAssetsOwnerService.deleteRussianAssetsOwner(id);
    }
}
