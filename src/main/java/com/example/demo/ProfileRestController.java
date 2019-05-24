package com.example.demo;

import java.net.URI;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@org.springframework.context.annotation.Profile("classic")
public class ProfileRestController {

	private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
	private final ProfileService profileRepository;

	ProfileRestController(ProfileService profileRepository) {
		this.profileRepository = profileRepository;
	}

	@GetMapping
	Publisher<Profile> getAll() {
		return profileRepository.all();
	}

	@GetMapping("/{id}")
	Publisher<Profile> getById(@PathVariable("id") String id) {
		return this.profileRepository.get(id);
	}

	@PostMapping
	Publisher<ResponseEntity<Profile>> create(@RequestBody Profile profile) {
		return this.profileRepository
			.create(profile.getEmail())
			.map(p -> ResponseEntity.created(URI.create("/profiles/" + p.getId()))
				.contentType(mediaType)
				.build());
	}

	@DeleteMapping("/{id}")
	Publisher<Profile> deleteById(@PathVariable String id) {
		return this.profileRepository.delete(id);
	}

	@PutMapping("/{id}")
	Publisher<ResponseEntity<Profile>> updateById(@PathVariable String id, @RequestBody Profile profile) {
		return Mono
			.just(profile)
			.flatMap(p -> this.profileRepository.update(id, p.getEmail()))
			.map(p -> ResponseEntity
				.ok()
				.contentType(this.mediaType)
				.build());
	}
}
