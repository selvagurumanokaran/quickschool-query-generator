##Overview
Let’s say we are developing a reporting module. With this module, the end-user can create
tabular reports by dragging and dropping fields onto the tabular report.
The user will see a menu called “Report Designer”. There, the user can create and edit
reports.
When the user edits a report, the user will be presented with the report designer. There,
the user can select from a list of fields that can be reported on, and drag them around to
define the columns of a tabular report.
When the user clicks on “Run”, an SQL statement will be automatically generated and run
on the database.
Your task is to develop the java code that generates the SQL statement based on the fields
the user has selected.

###Your Task
Your task is to develop, in Java, the SQL statement generator. We’ll keep things as simple as
possible for this task. Focus is on class design and the generator algorithm.
Requirements:
Classes:
● Design an Entity class - this represents a table in the database.
● Design a Field class - this represents a field in a table. Each field belongs to an Entity.
● Design a Join class - this represents the join table between any two tables. It has two
fields: the primary key on the two tables.
● Design an EntityLookup singleton class. This singleton will instantiate, in its
constructor, a bunch of Entity, Field and Join objects that represent the SQL tables
and fields of interest. So, in other words, the schema definition exists in memory,
and does not need to be persisted anywhere else.
● Just to clarify, we aren’t designing any Data Access Objects (DAO) here.
Algorithm class:
● Design an SqlGenerator class. Given a list of Fields, and Joins, generate the
necessary standard SQL. It’s fine to Just output a string of the SQL to the console.
Ignore sort order.
● The method may look something like this: public String generate(List<Field> fields,
List<Join> joins).
● Notice no Entities are passed in. That’s because each Field knows the Entity it
belongs to.
● If there’s something wrong with the arguments passed such that proper SQL cannot
be generated, throw an appropriate exception.

○ If zero fields are passed in, throw an exception.
○ If the fields belong to more than one entity, ensure enough joins are present
to link all the entities together. Otherwise, throw an exception.