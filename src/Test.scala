import Provider.*

final case class User(name: String, email: String)

class UserSubscription(emailService: EmailService, db: UserDatabase):
  def subscribe(user: User): Unit =
    emailService.email(user)
    db.insert(user)

object UserSubscription:
  def apply()(using Provider[(EmailService, UserDatabase)]): UserSubscription =
    new UserSubscription(provided[EmailService], provided[UserDatabase])

class EmailService:
  def email(user: User): Unit = println(s"You've just been subscribed to RockTheJVM. Welcome, ${user.name}")

class UserDatabase(pool: ConnectionPool):
  def insert(user: User): Unit = pool
    .get()
    .runQuery(s"insert into subscribers(name, email) values ${user.name} ${user.email}")

object UserDatabase:
  def apply()(using Provider[(ConnectionPool)]): UserDatabase = new UserDatabase(provided[ConnectionPool])

class ConnectionPool(n: Int):
  def get(): Connection =
    println(s"Acquired connection")
    Connection()

class Connection():
  def runQuery(query: String): Unit = println(s"Executing query: $query")

// https://github.com/scala/scala3/blob/main/tests/run/Providers.scala
// https://youtu.be/gLJOagwtQDw
object Test:
  def main(args: Array[String]): Unit =
    given Provider[EmailService]     = provide(EmailService())
    given Provider[ConnectionPool]   = provide(ConnectionPool(10))
    given Provider[UserDatabase]     = provide(UserDatabase())
    given Provider[UserSubscription] = provide(UserSubscription())

    def subscribe(user: User)(using Provider[UserSubscription]): Unit =
      val sub = UserSubscription()
      sub.subscribe(user)

    subscribe(User("Daniel", "daniel@RocktheJVM.com"))
    subscribe(User("Martin", "odersky@gmail.com"))
