Class Filters
=============

Class filters are patterned after the Java FileFilter class and are used to filter classes.  The default classes are defined in <code>org.xenei.junit.contract.filter</code> package.

The filters are implemented as functions and can be parsed from strings.  The case of the function name does not matter.  Methods that  take string matching arguments (e.g. prefix) have arguments that enable or disable case matching.


 
abstract
--------

The class must be abstract.

**arguments:** none.

**example:** abstract()

and
---

Performs a logical and on two or more class filters functions.

**arguments:** two or more other class filters.

**example:** and( abstract(), wildcard( *test* ) )

annotation
----------

The class must be an annotation.

**arguments:** none

**example:** annotation()

false
-----

Always evaluates to false.
		
**arguments:** none

**example:** false()
		
hasAnnotation
-------------

The class must have the specified annotation.
		
**arguments:** the class name of an annotation (e.g. org.xenei.junit.contract.Contract).  Class must be found in the class loader of the current thread. 

**example:** hasAnnotation( org.xenei.junit.contract.Contract )


interface
---------

The class must be an interface
		
**arguments:** none

**example:** interface()


name
----

The class name must match
		
**arguments:** An optional sensitive flag followed by one or more fully qualified class names.  if the sensitive flag is not specified _Sensitive_ is assumed.

**example:** name( Sensitive, org.xenei.junit.contract.Contract, org.xenei.junit.contract.Dynanamic )
**example:** name( org.xenei.junit.contract.Contract, org.xenei.junit.contract.Dynanamic )
**example:** name( Insensitive, org.xenei.junit.contract.Contract, org.xenei.junit.contract.Dynanamic )

not
---

Negates another filter.

**arguments:** another class filter.

**example:** not( abstract() )


or
--

Performs a logical _or_ on two or more filters.

**arguments:** two or more other class filters.

**example:** or( abstract(), wildcard( *test* ) )


prefix
------

The class name must match the prefix.  Useful for stripping out inner classes, or for selecting
entire package trees.

**arguments:** An optional sensitive flag followed by one or more class name prefixes.  if the sensitive flag is not specified _Sensitive_ is assumed.

**example:** prefix( Sensitive, org.xenei.junit.contract )
**example:** prefix( org.xenei.junit.contract )
**example:** prefix( Insensitive, org.xenei.junit.contract )


regex
-----

The class name must match the regular expression.

**arguments:** An optional sensitive flag followed by one regular expression.  if the sensitive flag is not specified _Sensitive_ is assumed.

**example:** regex( Sensitive, ^.*Test.*$ )
**example:** regex( ^.*Test.*$  )
**example:** regex( Insensitive, ^.*Test.*$ )

suffix
------

The class name must match the suffix.  

**arguments:** An optional sensitive flag followed by one or more class name prefixes.  if the sensitive flag is not specified _Sensitive_ is assumed.

**example:** suffix( Sensitive, Filter )
**example:** suffix( Filter )
**example:** suffix( Insensitive, filter )

true
----

Always evaluates to true.
		
**arguments:** none

**example:** true()


wildcard
--------

Matches the class name using the standard asterisk (*) to match multiple characters and the question mark (?) to match single characters.  Not as powerful as the regex filter but easier to use.

**arguments:** An optional sensitive flag followed by one regular expression.  if the sensitive flag is not specified _Sensitive_ is assumed.

**example:** wildcard( Sensitive, *Test? )
**example:** wildcard( *Test?  )
**example:** wildcard( Insensitive, *Test? )


