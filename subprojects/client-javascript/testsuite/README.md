Note: the version of tsUnit.ts in this folder seems to require version 1.4.1 of tsc. Using 1.5.0-beta causes the
following error: tsUnit.ts(262,37): error TS2345: Argument of type 'Event' is not assignable to parameter of type 'HashChangeEvent'.
Here is how you install version 1.4.1 of tsc:

  npm install -g typescript@1.4.1 

After changing any of the tests or the test suite make sure to run
    /usr/local/bin/tsc --out AllTestsInOne.js AllTests.ts
before
    open AllUnitTests.html
or starting the tests via node.js (e.g. via IDE plugin) in which case you
may want to configure the starter to call the tsc automatically before running
the tests.