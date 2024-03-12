//package fund.data.assets.model.asset.user;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.Table;
//
//import jakarta.validation.constraints.NotBlank;
//
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import org.springframework.context.annotation.Primary;
//
///**
// * Сущность - собственник активов - гражданин РФ. Для идентификации используются данные из паспорта гражданина РФ.
// * Класс -  наследник абстрактного AssetsOwner. Используется по дефолту (@Primary).
// * @since 0.0.1-alpha
// * @author MarkDementev a.k.a JavaMarkDem
// */
//@Primary
//@Entity
//@Table(name = "Asset owners with Russia Federation citizenship")
//@NoArgsConstructor
//@Getter
//@Setter
//public class RussianAssetsOwner extends AssetsOwner {
//    @NotBlank
//    private String Patronymic;
//
////    private String Sex;
//
////    private String passportSeries;
//
////    private String passportNumber;
//}
