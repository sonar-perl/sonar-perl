use lib 'lib';
use Test::More;
use Sample::Project;

my $project = Sample::Project->new();
is($project->hello, 'hello', 'should be hello');
fail("should fail");
done_testing;

1;
