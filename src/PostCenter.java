import java.util.logging.Level;
import java.util.logging.Logger;

public class PostCenter {
    public static void main(String[] args) {
        //
    }

    /*
       Интерфейс: сущность, которую можно отправить по почте.
       У такой сущности можно получить от кого и кому направляется письмо.
     */

    public static interface Sendable {
        String getFrom();

        String getTo();
    }

    // У интрефейса  Sendable есть два наследника, объединенные следующим абстрактным классом:
    /*
    Абстрактный класс,который позволяет абстрагировать логику хранения
    источника и получателя письма в соответствующих полях класса.
            */
    public static abstract class AbstractSendable implements Sendable {

        protected final String from;
        protected final String to;

        public AbstractSendable(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String getFrom() {
            return from;
        }

        @Override
        public String getTo() {
            return to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AbstractSendable that = (AbstractSendable) o;

            if (!from.equals(that.from)) return false;
            if (!to.equals(that.to)) return false;

            return true;
        }

    }

    // Первый класс описывает обычное письмо, в котором находится только текстовое сообщение.
    /*
      Письмо, у которого есть текст, который можно получить с помощью метода `getMessage`
     */
    public static class MailMessage extends AbstractSendable {

        private final String message;

        public MailMessage(String from, String to, String message) {
            super(from, to);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            MailMessage that = (MailMessage) o;

            if (message != null ? !message.equals(that.message) : that.message != null) return false;

            return true;
        }
    }

    // Второй класс описывает почтовую посылку:

    /*
      Посылка, содержимое которой можно получить с помощью метода `getContent`
     */
    public static class MailPackage extends AbstractSendable {
        private final Package content;

        public MailPackage(String from, String to, Package content) {
            super(from, to);
            this.content = content;
        }

        public Package getContent() {
            return content;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            MailPackage that = (MailPackage) o;

            if (!content.equals(that.content)) return false;

            return true;
        }

    }

    /*
        Класс, который задает посылку. У посылки есть текстовое описание содержимого и целочисленная ценность.
    */

    public static class Package {
        private final String content;
        private final int price;

        public Package(String content, int price) {
            this.content = content;
            this.price = price;
        }

        public String getContent() {
            return content;
        }

        public int getPrice() {
            return price;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Package aPackage = (Package) o;

            if (price != aPackage.price) return false;
            if (!content.equals(aPackage.content)) return false;

            return true;
        }
    }
    /*
     Интерфейс, который задает класс, который может каким-либо образом обработать почтовый объект.
     */

    public static interface MailService {
        Sendable processMail(Sendable mail);
    }

    /*
    Класс, в котором скрыта логика настоящей почты
    */
    public static class RealMailService implements MailService {

        @Override
        public Sendable processMail(Sendable mail) {
            // Здесь описан код настоящей системы отправки почты.
            return mail;
        }
    }

    public static final String AUSTIN_POWERS = "Austin Powers";
    public static final String WEAPONS = "weapons";
    public static final String BANNED_SUBSTANCE = "banned substance";


    /*
    1) UntrustworthyMailWorker – класс, моделирующий ненадежного работника почты, который вместо того, чтобы передать
    почтовый объект непосредственно в сервис почты, последовательно передает этот объект набору третьих лиц, а затем,
    в конце концов, передает получившийся объект непосредственно экземпляру RealMailService. У UntrustworthyMailWorker
    должен быть конструктор от массива MailService ( результат вызова processMail первого элемента массива передается на
    вход processMail второго элемента, и т. д.) и метод getRealMailService, который возвращает ссылку на внутренний
    экземпляр RealMailService.
    */

    public static class UntrustworthyMailWorker implements MailService {
        private RealMailService rms; // создали экземпляр RealMailService
        private MailService[] mailServices;// список третьих лиц

        public UntrustworthyMailWorker(MailService[] mailServices){ // У UntrustworthyMailWorker должен быть
                                                                    // конструктор от массива MailService
            this.mailServices = mailServices;
            this.rms = new RealMailService();
        }

        // тесты требуют наличия публичного метода getRealMailService без аргументов поэтому он здесь
        public MailService getRealMailService(){ // метод возражающий ссылку на внутренни экземпляр RealMailService.
            return rms;
        }


        @Override
        public Sendable processMail(Sendable mail) { // конструктор от массива MailService моделирующий
                                                     // ненадежного работника
            // Sendable processed = mail; Устаревшая реализация 23:29(07/04)

            for (int i = 0; i < mailServices.length; i++) { // перебор третьих лиц
                mail = mailServices[i].processMail(mail);// тут для первого будет вызван метод и подан второму и тд
            }
            return rms.processMail(mail); // в конце цикла передает получившейся объект в RealMailService
        }
    }

    /*
     2) Spy – шпион, который логгирует о всей почтовой переписке, которая проходит через его руки. Объект конструируется
    от экземпляра Logger, с помощью которого шпион будет сообщать о всех действиях. Он следит только за объектами класса
     MailMessage и пишет в логгер следующие сообщения (в выражениях нужно заменить части в фигурных скобках на значения
        полей почты):
        2.1) Если в качестве отправителя или получателя указан "Austin Powers", то нужно написать в лог сообщение с
        уровнем WARN: Detected target mail correspondence: from {from} to {to} "{message}"
        2.2) Иначе, необходимо написать в лог сообщение с уровнем INFO: Usual correspondence: from {from} to {to}
     */
    public static class Spy implements MailService {
        private final Logger logger; // /Присвоим внутренний логер
        public Spy(final Logger logger) { // Объект конструирующийся от экземпляра Logger
            this.logger = logger;
        }

        @Override
        public Sendable processMail(Sendable mail) { //
            // (mail instanceof MailMessage) - возможный вариант изменения проверки в if
            if(mail.getClass() == MailMessage.class) {
                MailMessage mailMes = (MailMessage) mail; // создает объект MailMessage и присваивает ему входящий
                                                           // Sendable mail
                //Если в качестве отправителя или получателя указан "Austin Powers", то нужно написать в лог сообщение

                String from = mailMes.getFrom(); // определяем переменную для улучшения читаемости
                String to = mailMes.getTo();     // определяем переменную для улучшения читаемости
                //
                if (from.equals(AUSTIN_POWERS) || to.equals(AUSTIN_POWERS)) {
                    this.logger.log(Level.WARNING, "Detected target mail correspondence: from {0} to {1} \"{2}\"",
                            new Object[]{mailMes.getFrom(), mailMes.getTo(), mailMes.getMessage()});

                } else { // Иначе, необходимо написать в лог сообщение с уровнем INFO:
                    // Usual correspondence: from {from} to {to}
                    logger.log(Level.INFO, "Usual correspondence: from {0} to {1}",
                            new Object[]{mailMes.getFrom(), mailMes.getTo()});
                }
            }
            return mail; // возвращает почту в вызвавший его метод
        }
    }

    /*
     3) Thief – вор, который ворует самые ценные посылки и игнорирует все остальное. Вор принимает в конструкторе
        переменную int – минимальную стоимость посылки, которую он будет воровать. Также, в данном классе должен
         присутствовать метод getStolenValue, который возвращает суммарную стоимость всех посылок, которые он своровал.
       Воровство происходит следующим образом: вместо посылки, которая пришла вору, он отдает новую, такую же, только
       нулевой ценностью и содержимым посылки "stones instead of {content}".
       */
    public static class Thief implements MailService {
        private int minPrice = 0; //минимальная стоимость посылки, которую он будет воровать.
        private int stolenPrice = 0; // Для хранения суммы украденного
        public Thief(int minPrice){ // конструктор вора который принимает минимальную стоимость потенциально украденного
            this.minPrice = minPrice;
            this.stolenPrice = 0; // при создании Вора он еще ничего не украл, сумма ровна 0
        }

        public int getStolenValue(){ // возвращает суммарную стоимость всех посылок
            return stolenPrice;
        }
        @Override
        public Sendable processMail(Sendable mail) {

            // (mail instanceof MailMessage) - возможный вариант изменения проверки в if
            if(mail.getClass() == MailPackage.class) { // проверка что отправления является посылкой

                //Устаревшая реализация от 00:05 (08/04)
                // Package pac = ((MailPackage)mail).getContent();

                MailPackage mailMas = (MailPackage) mail; //Создаем объект посылка, и присваиваем ему наше отправление

                /**Наша посылка состоит из:
                 *   private final Package content;
                 *
                 *
                 *   a Package, в свою очередь, состоит из:
                 *   public static class Package {
                 private final String content;
                 private final int price;
                 *
                 *ПОэтому для того, что бы получить стоимость посылки, надо взять метод getPrice () от
                 * метода getContent () объекта mail2.
                 */
                //Устаревшая реализация от 00:08 (08/04)
                /*
                if(pac.getPrice() >= minPrice){
                    stolenPrice += pac.getPrice();
                    mail = new MailPackage(mail.getFrom(), mail.getTo(),new Package("stones instead of "
                            + pac.getContent(), 0));
                }
                 */
                if (mailMas.getContent().getPrice() >= this.minPrice){ // Вор проверяет ценна ли посылка
                    this.stolenPrice += mailMas.getContent().getPrice(); //Вор совершает
                                                        // преступление и его счетчик увеличивается на стоимость посылки
                    // Вместо украденой посылки, вор отдает новую такую же только с 0 ценностью. и содержимым посылки
                    // "stones instead of {content}"*/
                    return new MailPackage(mailMas.getFrom(),mailMas.getTo(),
                            new Package(
                                    "stones instead of "+ mailMas.getContent().getContent(),0));
                }

            }
            return mail; //Вор отдает посылки без изменений, которые ему не подошли
        }
    }
    /*
     4) Inspector – Инспектор, который следит за запрещенными и украденными посылками и бьет тревогу в виде исключения,
       если была обнаружена подобная посылка. Если он заметил запрещенную посылку с одним из запрещенных содержимым
     ("weapons" и "banned substance"), то он бросает IllegalPackageException. Если он находит посылку, состаящую из
      камней (содержит слово "stones"), то тревога прозвучит в виде StolenPackageException. Оба исключения вы должны
      объявить самостоятельно в виде непроверяемых исключений.
     */

    // Устаревшая реализация от 00:52 (08/04)
    /*
    public static class IllegalPackageException extends RuntimeException {}
    public static class StolenPackageException extends RuntimeException {}
     */

    public static class Inspector implements MailService {
        @Override
        public Sendable processMail(Sendable mail) {
            // (mail instanceof MailMessage) - возможный вариант изменения проверки в if
            if (mail.getClass() == MailPackage.class) { // проверка посылка ли пришла
                MailPackage mailMas = (MailPackage) mail;

                //Устаревшая реализация от 00:57 (08/04)
                /*Package pac = ((MailPackage)mail).getContent();
                String content = pac.getContent();
                if(content.indexOf("stones instead of ") == 0) {
                    throw new StolenPackageException();
                } else if(content.equals(WEAPONS) || content.equals(BANNED_SUBSTANCE)){
                    throw new IllegalPackageException();
                }
                 */


                /*Если была замечена посылка с запрещенным содержимым, что-то из "weapons" и "banned substance"),
                 то он бросает IllegalPackageException.*/

                if (mailMas.getContent().getContent().contains(WEAPONS) ||
                        mailMas.getContent().getContent().contains(BANNED_SUBSTANCE)) {
                    throw new IllegalPackageException();
                }

                // Если будет найдена посылка состоящая из камней "stones" то будет
                // брошена ошибка StolenPackageException

                if (mailMas.getContent().getContent().contains("stones")) {
                    throw new StolenPackageException();
                }
                return mailMas;
            }
            return mail;
        }
    }
    /*Оба исключения мы должны объявить самостоятельно в виде непроверяемых исключений.  */
    public static class StolenPackageException extends RuntimeException {
        public StolenPackageException() {
            super("Discovered the theft from the parcel!");
        }
    }

    public static class IllegalPackageException extends RuntimeException {
        public IllegalPackageException() {
            super("IllegalPackageException!");
        }
    }

}

