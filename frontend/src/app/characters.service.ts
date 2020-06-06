// import {Injectable} from '@angular/core';
// import {Character, JsonReturn} from './apiobjects';
// import {Observable} from 'rxjs';
//
// import {HttpClient} from '@angular/common/http';
//
// @Injectable({
//   providedIn: 'root'
// })
// export class CharactersService {
//   private characterUrl = 'https://www.loot.senorpez.com/campaigns/1/characters';
//
//   constructor(private http: HttpClient) { }
//
//   getCharacters(): Observable<JsonReturn> {
//     return this.http.get<JsonReturn>(this.characterUrl);
//   }
//
//   getCharacter(id: number): Observable<Character> {
//     const url = `${this.characterUrl}/${id}`;
//     return this.http.get<Character>(url);
//   }
// }
