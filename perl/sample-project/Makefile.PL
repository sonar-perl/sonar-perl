use ExtUtils::MakeMaker;

WriteMakefile(
	NAME         => "Sample::Project",
	VERSION_FROM => "lib/Sample/Project.pm",
	MIN_PERL_VERSION => 5.10.1,
	PREREQ_PM    => {

		# Require Test::More at least 0.47
		"Test::More" => "0.47",
	}
);
