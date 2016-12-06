package Sample::Project;

$VERSION = '0.1';

sub new {
    my $class = shift;
    my %args  = @_;
    my $self  = bless \%args, $class;
    return $self;
}

# say hello
sub hello {
    return "hello";
}

# say goodbye
sub goodbye {
    return "goodbye";
}

=pod

=head1 NAME

C<IO::Socket::IP> - Family-neutral IP socket supporting both IPv4 and IPv6

=head1 SYNOPSIS

 use IO::Socket::IP;

 my $sock = IO::Socket::IP->new(
    PeerHost => "www.google.com",
    PeerPort => "http",
    Type     => SOCK_STREAM,
 ) or die "Cannot construct socket - $@";

 my $familyname = ( $sock->sockdomain == PF_INET6 ) ? "IPv6" :
                  ( $sock->sockdomain == PF_INET  ) ? "IPv4" :
                                                      "unknown";

 printf "Connected to google via %s\n", $familyname;

=head1 DESCRIPTION

This module provides a protocol-independent way to use IPv4 and IPv6 sockets,
intended as a replacement for L<IO::Socket::INET>. Most constructor arguments
and methods are provided in a backward-compatible way. For a list of known
differences, see the C<IO::Socket::INET> INCOMPATIBILITES section below.

It uses the C<getaddrinfo(3)> function to convert hostnames and service names
or port numbers into sets of possible addresses to connect to or listen on.
This allows it to work for IPv6 where the system supports it, while still
falling back to IPv4-only on systems which don't.

=cut

1;
