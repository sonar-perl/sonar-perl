## PUBLIC METHOD
sub match_any {
    my $self = shift;
    my ($tail, $pat);
    if   ($self->case) { $tail = '/i' }
    else               { $tail = '/' }
    my $code = <<EOCODE;
sub {
      use utf8;
EOCODE
    $code .= <<EOCODE if @_ > 5;
      study;
EOCODE
    for $pat (@_) {
        $code .= <<EOCODE;
      return 1 if /$pat$tail;
EOCODE
    }
    $code .= "}\n";
    my $function = eval $code;
    die "bad pattern: $EVAL_ERROR" if $EVAL_ERROR;
    return $function;
}
