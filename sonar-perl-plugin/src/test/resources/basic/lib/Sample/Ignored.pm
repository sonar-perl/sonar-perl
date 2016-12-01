# sub ab {} this should be ignored.
sub proto($$); # this is a proto, and should get ignored.

# anonymous sub
sub {
}

### EDGE CASES TO BE IMPROVED:
# unfortunately this gets ignored
; sub mysub {}

# unfortunately this gets ignored
sub
 mysub2 {}

