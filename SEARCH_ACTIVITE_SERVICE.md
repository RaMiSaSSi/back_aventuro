# Service Angular pour la recherche d'activités

## Service TypeScript

```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../Environement/environement';

export interface ActiviteDTO {
  id: string;
  titre: string;
  description: string;
  lieu: string;
  categorie: string;
  prix: number;
  duree: number;
  images: string[];
  video?: string;
  estActive: boolean;
  heureDebut?: string;
  heureFin?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ActiviteService {
  private apiUrl = `${environment.baseUrl}/activites`;

  constructor(private http: HttpClient) {}

  // Récupérer toutes les activités
  getAllActivites(): Observable<ActiviteDTO[]> {
    return this.http.get<ActiviteDTO[]>(this.apiUrl);
  }

  // Récupérer une activité par ID
  getActiviteById(id: string): Observable<ActiviteDTO> {
    return this.http.get<ActiviteDTO>(`${this.apiUrl}/${id}`);
  }

  // Récupérer les catégories
  getCategories(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/categories`);
  }

  // Récupérer les activités par catégorie
  getActivitesByCategorie(categorie: string): Observable<ActiviteDTO[]> {
    return this.http.get<ActiviteDTO[]>(`${this.apiUrl}/categories/${categorie}/activites`);
  }

  // Rechercher des activités par mot-clé
  searchActivites(keyword: string): Observable<ActiviteDTO[]> {
    const params = new HttpParams().set('keyword', keyword);
    return this.http.get<ActiviteDTO[]>(`${this.apiUrl}/search`, { params });
  }
}
```

## Exemple d'utilisation dans un composant

```typescript
import { Component, OnInit } from '@angular/core';
import { ActiviteService, ActiviteDTO } from './activite.service';

@Component({
  selector: 'app-activite-search',
  template: `
    <div class="search-container">
      <input 
        type="text" 
        [(ngModel)]="searchKeyword" 
        (input)="onSearch()" 
        placeholder="Rechercher une activité..."
        class="search-input"
      />
      
      <div class="results" *ngIf="activites.length > 0">
        <div *ngFor="let activite of activites" class="activite-card">
          <h3>{{ activite.titre }}</h3>
          <p>{{ activite.description }}</p>
          <p><strong>Lieu:</strong> {{ activite.lieu }}</p>
          <p><strong>Prix:</strong> {{ activite.prix }} €</p>
          <p><strong>Durée:</strong> {{ activite.duree }} heures</p>
        </div>
      </div>
      
      <div *ngIf="searchKeyword && activites.length === 0" class="no-results">
        Aucune activité trouvée pour "{{ searchKeyword }}"
      </div>
    </div>
  `,
  styles: [`
    .search-container {
      padding: 20px;
    }
    
    .search-input {
      width: 100%;
      padding: 12px;
      font-size: 16px;
      border: 2px solid #ddd;
      border-radius: 8px;
      margin-bottom: 20px;
    }
    
    .activite-card {
      border: 1px solid #e0e0e0;
      border-radius: 8px;
      padding: 16px;
      margin-bottom: 16px;
      background: white;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    
    .no-results {
      text-align: center;
      color: #999;
      padding: 40px;
    }
  `]
})
export class ActiviteSearchComponent implements OnInit {
  searchKeyword: string = '';
  activites: ActiviteDTO[] = [];

  constructor(private activiteService: ActiviteService) {}

  ngOnInit(): void {
    // Charger toutes les activités au démarrage
    this.loadAllActivites();
  }

  loadAllActivites(): void {
    this.activiteService.getAllActivites().subscribe({
      next: (data) => {
        this.activites = data;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des activités:', error);
      }
    });
  }

  onSearch(): void {
    if (this.searchKeyword.trim() === '') {
      // Si la recherche est vide, charger toutes les activités
      this.loadAllActivites();
      return;
    }

    // Effectuer la recherche
    this.activiteService.searchActivites(this.searchKeyword).subscribe({
      next: (data) => {
        this.activites = data;
      },
      error: (error) => {
        console.error('Erreur lors de la recherche:', error);
      }
    });
  }
}
```

## Utilisation avec debounce (éviter trop de requêtes)

```typescript
import { Component, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { ActiviteService, ActiviteDTO } from './activite.service';

@Component({
  selector: 'app-activite-search-optimized',
  templateUrl: './activite-search.component.html'
})
export class ActiviteSearchOptimizedComponent implements OnInit {
  searchKeyword: string = '';
  activites: ActiviteDTO[] = [];
  private searchSubject = new Subject<string>();

  constructor(private activiteService: ActiviteService) {}

  ngOnInit(): void {
    // Configurer la recherche avec debounce
    this.searchSubject.pipe(
      debounceTime(300), // Attendre 300ms après la dernière frappe
      distinctUntilChanged(), // Ne rechercher que si le texte a changé
      switchMap(keyword => {
        if (keyword.trim() === '') {
          return this.activiteService.getAllActivites();
        }
        return this.activiteService.searchActivites(keyword);
      })
    ).subscribe({
      next: (data) => {
        this.activites = data;
      },
      error: (error) => {
        console.error('Erreur lors de la recherche:', error);
      }
    });

    // Charger toutes les activités au démarrage
    this.activiteService.getAllActivites().subscribe({
      next: (data) => {
        this.activites = data;
      }
    });
  }

  onSearch(): void {
    this.searchSubject.next(this.searchKeyword);
  }
}
```

## Endpoints disponibles

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/activites` | Récupère toutes les activités |
| GET | `/activites/{id}` | Récupère une activité par ID |
| GET | `/activites/categories` | Récupère toutes les catégories |
| GET | `/activites/categories/{categorie}/activites` | Récupère les activités d'une catégorie |
| GET | `/activites/search?keyword={keyword}` | Recherche des activités par mot-clé |

## Exemple d'utilisation de l'endpoint de recherche avec Postman

**GET** `http://localhost:8080/activites/search?keyword=buggy`

**Réponse:**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "titre": "Sortie Buggy",
    "description": "Vivez une sortie buggy pleine de sensations !",
    "lieu": "Djerba",
    "categorie": "AVENTURE",
    "prix": 150.00,
    "duree": 2,
    "images": ["/activites/images/buggy1.jpg"],
    "estActive": true
  }
]
```

La recherche fonctionne sur les champs:
- **Titre** de l'activité
- **Description** de l'activité
- **Lieu** de l'activité

La recherche est **insensible à la casse** et trouve les correspondances **partielles**.

