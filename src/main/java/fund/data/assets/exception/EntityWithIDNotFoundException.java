package fund.data.assets.exception;

/**
 * Исключение - наследник NullPointerException.
 * Вызывается при неудачном поиске сущности в БД, когда её по указанному id не существует. Сообщает юзеру название
 * сущности и введённый id, чтобы при повторном запросе юзер не повторялся.
 * @since 0.0.2-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
public class EntityWithIDNotFoundException extends NullPointerException {
    public static final String MESSAGE = " entity with this id doesn't exist! Wrong id is ";

    public EntityWithIDNotFoundException(String entityName, Long id) {
        super(entityName + MESSAGE + id);
    }
}