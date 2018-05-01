package com.movie.test;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@Component 
public class MovieResource {

	@Autowired
	private MoviesRepository moviesRepository;

	// json format
	@GetMapping("/movie")
	public List<Movie> retriveAllMovies() {
		return moviesRepository.findAll();
	}

	// text format
	@GetMapping("/movies")
	public String returnMovies() {
		StringBuilder b = new StringBuilder();
		for (Movie m : moviesRepository.findAll()) {
			b.append(m.text() + "\n");
		}
		return b.toString();
	}

	// text format
	@GetMapping("/movie/{id}/text")
	public String retriveMovie(@PathVariable Long id) {
		Optional<Movie> movie = moviesRepository.findById(id);
		if (!movie.isPresent())
			throw new MovieNotFoundException("id - " + id);
		return movie.get().toString();
	}

	// json format
	@GetMapping("/movie/{id}/json")
	public Movie retriveMovieJson(@PathVariable Long id) {
		Optional<Movie> movie = moviesRepository.findById(id);
		if (!movie.isPresent())
			throw new MovieNotFoundException("id - " + id);
		return movie.get();
	}

	@DeleteMapping("/movie/{id}")
	public void deleteMovie(@PathVariable Long id) {
		moviesRepository.deleteById(id);
	}

	@PostMapping("/movie")
	public ResponseEntity<Object> addMovie(@RequestBody Movie movie) {
		Movie savedMovie = moviesRepository.save(new Movie(movie.getName(), movie.getDescription(), movie.getYear()));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedMovie.getId())
				.toUri();

		return ResponseEntity.created(location).build();
	}

	@PutMapping("/movie/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Object> updateMovies(@RequestBody Movie movie, @PathVariable Long id) {
		Optional<Movie> movieOptional = moviesRepository.findById(id);

		if (!movieOptional.isPresent())
			return ResponseEntity.notFound().build();

		movie.setId(id);
		moviesRepository.save(movie);

		return ResponseEntity.noContent().build();

	}

}
