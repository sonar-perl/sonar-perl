=head1 NAME

file_processor.pm -- module for process the file

=head1 AUTHOR

QingYun(Ben) Deng

=cut

package file_processor;
use strict;
use warnings;
use File::Copy;
use Data::Dumper;

use base qw(Class::Accessor);
file_processor->mk_accessors(qw(
    records sets
    errors file outfile
	date time summary
    data_in data_out archive_dir
    record_number
));

my @records_spec = (
	record_code 				=> 3,
	client_type 				=> 4,
	client_number 				=> 4,
	account_number 				=> 4,
	subaccount_number 			=> 4,
	opposite_party_code 		=> 6,
	product_group_code 			=> 2,
	exchange_code 				=> 4,
	symbol 						=> 6,
	expiration_date 			=> 8,
	currency_code 				=> 3,
	movement_code 				=> 2,
	buy_sell_code 				=> 1,
	quantity_long_sign 			=> 1,
	quantity_long 				=> 10,
	quantity_short_sign 		=> 1,
	quantity_short 				=> 10,
	exch_broker_fee_dec 		=> 12, 
	exch_broker_fee_d_c 		=> 1,
	exch_broker_fee_cur_code 	=> 3,
	clearing_fee_dec 			=> 13, 
	clearing_fee_d_c 			=> 1,
	clearing_fee_cur_code 		=> 3,
	commission 					=> 13, 
	commission_d_c 				=> 1,
	commission_cur_code 		=> 3,
	transaction_date 			=> 8, 
	future_reference 			=> 6,
	ticket_number 				=> 6,
	external_number 			=> 6, 
	transaction_price_dec 		=> 15,
	trader_initials 			=> 6,
	opposite_trader_id 			=> 7, 
	open_close_code 			=> 1,
);
	
=head1 NAME

file_processor -- Process incoming files

=head1 SYNOPSIS

    use file_processor;
    my $p = file_processor->new({
        data_in => "/tmp",
        data_out => "/tmp",
        archive_dir => "/tmp",
        file => 'Input.txt', 
        outfile => 'Output.csv',
    });
    $p->process_files();

=head1 DESCRIPTION

Process incoming files .

=cut

sub new {
    my ($class, $args) = @_;
    my $self = {};
    bless $self, $class;
    $self->data_in($args->{data_in} || '.' );
    $self->data_out($args->{data_out}   || '.'  );
    $self->archive_dir($args->{archive_dir}   || '.'  );
    $self->file($args->{file} || 'Input.txt');
	$self->outfile($args->{outfile} || 'Output.csv');
	
	my ($sec,$min,$hour,$mday,$mon,$year) = localtime;
	$year = $year+1900;
	$mon += 1;
	if (length($mon)  == 1) {$mon = "0$mon";}
	if (length($mday) == 1) {$mday = "0$mday";}
	if (length($sec)  == 1) {$sec = "0$sec";}
	if (length($min)  == 1) {$min = "0$min";}
	if (length($hour) == 1) {$hour = "0$hour";}
	$self->date($year.$mon.$mday);
	$self->time($hour.$min.$sec);
	
    return $self;
}

sub process_files {
    my ($self) = @_;

	print "Processing file:".$self->data_in().'/'.$self->file()."\n"; 
    $self->parse_record();
    $self->process_record();
	print "File ".$self->file()." processed\n\n"; 
	$self->output_report();
	$self->logs();
	$self->cleanup($self->data_in(),$self->file());
}

=head2 output_report()

Output report

=cut

sub output_report {
	my ($self) = @_;
		
	if ($self->summary()) {
		my $repotfile = $self->data_out().'/'.$self->outfile();
		
		if (-e $repotfile){
			print $self->outfile()." exsiting.";
			$self->cleanup($self->data_out(),$self->outfile());
		}
		my $summary = $self->summary();
		print "Creating report ".$self->outfile()."\n"; 
		my $rpt;
		open ($rpt, ">$repotfile") or die "Can't open rpt file: $!";

		print $rpt "Client_Information,Product_Information,Total_Transaction_Amount\n";
		foreach my $client (keys %{$summary}){
			foreach my $product (keys %{$summary->{$client}}){
				print $rpt "$client,$product,$summary->{$client}{$product}\n";
			}
		}
		close $rpt;
		print "Report ".$self->outfile()." created\n\n"; 
    }
	else{
		print "\nThere is no recorrd for output report\n";
	}
}


=head2 process_record()

sum of all transaction amount by client and product.

=cut

sub process_record {
	my ($self) = @_;
	
	my $summary;
	if ($self->sets() and scalar @{$self->sets()} > 0) {
        foreach my $set (@{$self->sets()}) {
			$summary->{$set->{Client_Information}}{$set->{Product_Information}} += $set->{Total_Transaction_Amount} ;
		}
    }
	else{
		print "\nFile does not exist or file empty\n";
	}
	$self->summary($summary);
}


=head2 logs()

write log file.

=cut

sub logs {
    my ($self) = @_;

	my $date = $self->date();
    my $time = $self->time();
    my $logfile = $self->data_out().'/'.'log_'.$date.'.txt';
	
    my $log;
	my @errors;
	if ($self->errors() and @errors = @{$self->errors()}) {
		my $rcount = 0;
		
		open ($log, ">>$logfile") or die "Can't open log file: $!";
		print $log "H|$date|$time\n";

		foreach (@errors) { 
			$self->_print_error($log, $_->[1], $date, $time);
			$rcount++;
		}
		
		print $log "T|$rcount\n";
		close $log;
	} 
    return 1;
}

=head2 parse_record()

Given a list of filenames, parse them

=cut

sub parse_record {
	my ($self) = @_;

	$self->import_file();
	
	 my $recnum = 0;
	  my @records;
	 foreach my $r (@{$self->records()}) {
		 $self->record_number($recnum++);
		 push @records, $self->_make_record($r);

	 }
	$self->sets(\@records);

}

=head2 _make_record

Turns a record row into a hash for use.

=cut

sub _make_record {
    my ($self, $record) = @_;
    
	chomp $record;
	
    my @record_name;
	my @recorf_width;

	foreach my $idx (0 .. scalar @records_spec - 1){
		push @record_name, $records_spec[$idx] if ($idx % 2 == 0); 
		push @recorf_width, 'A'.$records_spec[$idx] if ($idx % 2 == 1); 
	}

	my @recs = unpack ((join ' ', @recorf_width), $record);
	
	my $set;
	foreach my $idx (0 .. scalar @record_name - 1){
		$set->{$record_name[$idx]} = $recs[$idx];
	}

	$self->error(200, 'quantity_long', $set->{quantity_long}) unless $set->{quantity_long} =~ /^\d{10}$/;
	$self->error(200, 'quantity_short', $set->{quantity_short}) unless $set->{quantity_short} =~ /^\d{10}$/;
	
	$set->{Client_Information} = $set->{client_type}.$set->{client_number}.$set->{account_number}.$set->{subaccount_number};
	$set->{Product_Information} = $set->{exchange_code}.$set->{product_group_code}.$set->{symbol}.$set->{expiration_date};
	$set->{Total_Transaction_Amount} = $set->{quantity_long} - $set->{quantity_short};
		
    return $set;
}

=head2 error()

Throws a hard error, as when a file is unprocessable.

Requires an error code (numeric) as an argument.

=cut

sub error {
    my ($self, $errcode, $field, $value) = @_;
    my %errors = (
         100 => 'File cannot be read',
		 101 => 'File does not exist',
		 102 => 'File is empty',
		 200 => 'It has invalid value',
    );

    $self->_add_error($errcode, $errors{$errcode}, $field, $value);
    return undef;
}

sub _add_error {
    my ($self, $code, $msg, $field, $value) = @_;
    if ($self->errors()) {
        my @errors = @{$self->errors()};
        push @errors, [$code, "$msg: $field $value"];
        $self->errors(\@errors);
    } else {
        $self->errors([[$code, "$msg: $field $value"]]);
    }
}

=head2 import_file()

Given a filename, opens the file, sucks in the records

=cut

sub import_file {
    my ($self) = @_;
	
	my $filename = $self->data_in().'/'.$self->file();
	
	# zero-length file
    if (not -s $filename){
		print $self->file()." is empty input file\nProcessed aborted!\n";
		$self->error(102, 'File', $self->file());
		$self->logs();
		exit;
	}

    my @lines;
    if (open (IN, $filename)) {
        while (<IN>) {
            push @lines, $_;
        }
        close IN;
    } else {
        $self->error(100, 'File', $self->file());
    }

    $self->records(\@lines);
}



sub _print_error {
    my ($self, $log, $error, $date, $time) = @_;
    print $log "$error\n";
}


=head2 cleanup()

Cleans up by removing processed files, etc.

=cut

sub cleanup {
    my ($self, $dir, $file) = @_;

    my $archdir = $self->archive_dir();
	my $date = $self->date();
    my $time = $self->time();
    return unless $archdir; # don't cleanup if we don't have a archive dir
	print "\n$file be removed to archive folder\n\n";
    move($dir.'/'.$file, $archdir.'/'.$date.'_'.$time.'_'.$file);
}

=head2 debug()

Does something appropriate with a message passed to it as an arg --
currently, just warn()'s.

=cut

sub debug {
    my ($self, $msg) = @_;
    warn $msg;
}

1;

